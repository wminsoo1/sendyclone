package com.example.sendyclone.domain.delivery.service;

import com.example.sendyclone.domain.delivery.exception.DeliveryErrorCode;
import com.example.sendyclone.domain.delivery.exception.DeliveryException;
import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import com.example.sendyclone.domain.delivery.model.dto.request.DeliverySaveRequest;
import com.example.sendyclone.domain.delivery.model.dto.request.DeliveryUpdateRequest;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliveryDetailResponse;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySaveResponse;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySummaryResponse;
import com.example.sendyclone.domain.delivery.model.entity.Delivery;
import com.example.sendyclone.domain.delivery.model.entity.StopOver;
import com.example.sendyclone.domain.delivery.repository.DeliveryRepository;
import com.example.sendyclone.domain.delivery.repository.StopOverRepository;
import com.example.sendyclone.domain.delivery.utils.ReservationNumberGenerator;
import com.example.sendyclone.domain.member.exception.MemberException;
import com.example.sendyclone.domain.member.model.entity.Member;
import com.example.sendyclone.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.sendyclone.domain.delivery.exception.DeliveryErrorCode.*;
import static com.example.sendyclone.domain.member.exception.MemberErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final MemberRepository memberRepository;
    private final StopOverRepository stopOverRepository;

    @Transactional
    public DeliverySaveResponse saveDelivery(DeliverySaveRequest deliverySaveRequest, Long memberId) { //나중에 결제 추가하면 배달 상태도 추가해줘야 함
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberException.fromErrorCode(MEMBER_NOT_FOUND));

        final Delivery delivery = deliverySaveRequest.toDelivery();
        delivery.updateMember(member);
        delivery.updateDeliveryFee(10000); //결제금액은 프론트에서 결정

        final Delivery savedDelivery;
        synchronized (this) { //서버 여러개 일 때 문제 생길듯 저장 할때는 @Lock 이용을 못함 잠글 레코드가 없음
            delivery.updateReservationNumber(ReservationNumberGenerator.withDate(deliveryRepository.count()));
            savedDelivery = deliveryRepository.save(delivery);
        }

        final List<DeliveryAddress> deliveryAddresses = saveStopOvers(deliverySaveRequest, savedDelivery);

        return DeliverySaveResponse.from(savedDelivery, deliveryAddresses);
    }

    @Transactional
    public void deleteDelivery(Long deliveryId) {
        final Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> DeliveryException.fromErrorCode(DELIVERY_NOT_FOUND));

        List<StopOver> stopOverByDeliveryId = stopOverRepository.findStopOverByDeliveryId(deliveryId);

        stopOverRepository.deleteAll(stopOverByDeliveryId);
        deliveryRepository.delete(delivery);
    }

    @Transactional
    public void updateDelivery(Long deliveryId, DeliveryUpdateRequest deliveryUpdateRequest) { //경유지 변경감지 봐야함
        final Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> DeliveryException.fromErrorCode(DELIVERY_NOT_FOUND));

        delivery.update(deliveryUpdateRequest);
        if (deliveryUpdateRequest.getDeliveryAddress() != null) {
            updateStopOver(deliveryId, deliveryUpdateRequest, delivery);
        }
    }

    public DeliveryDetailResponse getDeliveryDetail(String reservationNumber, Long memberId) {
        validatedMemberExisted(memberId);
        validatedReservationNumberExist(reservationNumber);

        final Delivery delivery = deliveryRepository.findDeliveryByReservationNumberAndMemberId(reservationNumber, memberId)
                .orElseThrow(() -> DeliveryException.fromErrorCode(DELIVERY_NOT_FOUND));

        final List<StopOver> stopOvers = stopOverRepository.findStopOverByDeliveryId(delivery.getId());
        final List<DeliveryAddress> deliveryAddresses = stopOvers.stream()
                .map(StopOver::getDeliveryAddress)
                .toList();

        return DeliveryDetailResponse.from(delivery, deliveryAddresses);
    }

    public List<DeliverySummaryResponse> getDeliveriesSummary(Long memberId) { //Page, Slice 코드 리팩토링 해야할수도
        validatedMemberExisted(memberId);

        final List<Delivery> deliveries = deliveryRepository.findDeliveriesByMemberId(memberId);

        final List<Long> deliveryIds = deliveries.stream()
                .map(Delivery::getId)
                .toList();

        final List<StopOver> stopOverByDeliveryIds = stopOverRepository.findStopOverByDeliveryIds(deliveryIds);
        final Map<Long, List<StopOver>> stopOversByDeliveryId = stopOverByDeliveryIds.stream()
                .collect(Collectors.groupingBy(stopOver -> stopOver.getDelivery().getId()));

        return deliveries.stream()
                .map(delivery -> {
                    int size = stopOversByDeliveryId.get(delivery.getId()).size();
                    return DeliverySummaryResponse.from(delivery, size);
                })
                .toList();
    }

    private void updateStopOver(Long deliveryId, DeliveryUpdateRequest deliveryUpdateRequest, Delivery delivery) {
        List<DeliveryAddress> stopOverAddresses = deliveryUpdateRequest.getStopOverAddresses();
        List<StopOver> stopOverByDeliveryId = stopOverRepository.findStopOverByDeliveryId(deliveryId);
        if (stopOverByDeliveryId == null) {
            DeliveryException.fromErrorCode(STOPOVER_NOT_FOUND);
        }

        List<StopOver> stopOversToDelete = findStopOversToDelete(stopOverByDeliveryId, stopOverAddresses);
        List<DeliveryAddress> stopOversToAdd = findDeliveryAddressToAdd(stopOverAddresses, stopOverByDeliveryId);

        for (StopOver stopOver : stopOversToDelete) {
            System.out.println("stopOver = " + stopOver);
        }
        stopOverRepository.deleteAll(stopOversToDelete);
        for (DeliveryAddress address : stopOversToAdd) {
            saveStopOver(address, delivery);
        }
    }

    private static List<DeliveryAddress> findDeliveryAddressToAdd(List<DeliveryAddress> stopOverAddresses, List<StopOver> stopOverByDeliveryId) {
        return stopOverAddresses.stream()
                .filter(deliveryAddress -> stopOverByDeliveryId.stream()
                        .noneMatch(stopOver -> stopOver.getDeliveryAddress().equals(deliveryAddress)))
                .toList();
    }

    private static List<StopOver> findStopOversToDelete(List<StopOver> stopOverByDeliveryId, List<DeliveryAddress> stopOverAddresses) {
        return stopOverByDeliveryId.stream()
                .filter(stopOver -> stopOverAddresses.stream()
                        .noneMatch(deliveryAddress -> deliveryAddress.equals(stopOver.getDeliveryAddress())))
                .toList();
    }

    private void saveStopOver(DeliveryAddress address, Delivery delivery) {
        StopOver stopOver = StopOver.builder()
                .deliveryAddress(address)
                .delivery(delivery)
                .build();
        stopOverRepository.save(stopOver);
    }

    private List<DeliveryAddress> saveStopOvers(DeliverySaveRequest deliverySaveRequest, Delivery delivery) {
        if (deliverySaveRequest.getStopOverAddresses().isEmpty()) {
            return Collections.emptyList();
        }

        final List<StopOver> stopOvers = deliverySaveRequest.toStopOvers();

        for (StopOver stopOver : stopOvers) {
            stopOver.updateDelivery(delivery);
            stopOverRepository.save(stopOver);
        }

        return stopOvers.stream()
                .map(StopOver::getDeliveryAddress)
                .toList();
    }

    private void validatedMemberExisted(Long memberId) {
        final boolean isMemberExist = memberRepository.existsById(memberId);
        if (!isMemberExist) {
            throw MemberException.fromErrorCode(MEMBER_NOT_FOUND);
        }
    }

    private void validatedReservationNumberExist(String reservationNumber) {
        final boolean isReservationNumberExist = deliveryRepository.existsByReservationNumber(reservationNumber);
        if (!isReservationNumberExist) {
            throw DeliveryException.fromErrorCode(RESERVATION_NUMBER_NOT_FOUND);
        }
    }

}
