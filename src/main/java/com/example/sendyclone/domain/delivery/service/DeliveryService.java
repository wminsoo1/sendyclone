package com.example.sendyclone.domain.delivery.service;

import com.example.sendyclone.domain.delivery.exception.DeliveryNotFoundException;
import com.example.sendyclone.domain.delivery.exception.ReservationNumberNotFoundException;
import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import com.example.sendyclone.domain.delivery.model.dto.request.DeliverySaveRequest;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliveryDetailResponse;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySaveResponse;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySummaryResponse;
import com.example.sendyclone.domain.delivery.model.entity.Delivery;
import com.example.sendyclone.domain.delivery.model.entity.StopOver;
import com.example.sendyclone.domain.delivery.repository.DeliveryRepository;
import com.example.sendyclone.domain.delivery.repository.StopOverRepository;
import com.example.sendyclone.domain.delivery.utils.ReservationNumberGenerator;
import com.example.sendyclone.domain.member.exception.MemberNotFoundException;
import com.example.sendyclone.domain.member.model.entity.Member;
import com.example.sendyclone.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .orElseThrow(MemberNotFoundException::new);

        final Delivery delivery = deliverySaveRequest.toDelivery();
        delivery.updateMember(member);
        delivery.updateDeliveryFee(10000); //결제금액은 프론트에서 결정

        final LocalDateTime deliveryDate = delivery.getDeliveryDate();

        final String reservationNumber = ReservationNumberGenerator.withDate(deliveryRepository.count());
        delivery.updateReservationNumber(reservationNumber);

        final Delivery savedDelivery = deliveryRepository.save(delivery);

        final List<DeliveryAddress> deliveryAddresses = saveStopOvers(deliverySaveRequest, savedDelivery);

        return DeliverySaveResponse.from(savedDelivery, deliveryAddresses);
    }

    public DeliveryDetailResponse getDeliveryDetail(String reservationNumber, Long memberId) {
        validatedMemberExisted(memberId);
        validatedReservationNumberExist(reservationNumber);

        final Delivery delivery = deliveryRepository.findDeliveryByReservationNumberAndMemberId(reservationNumber, memberId)
                .orElseThrow(DeliveryNotFoundException::new);

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
            throw new MemberNotFoundException();
        }
    }

    private void validatedReservationNumberExist(String reservationNumber) {
        final boolean isReservationNumberExist = deliveryRepository.existsByReservationNumber(reservationNumber);
        if (!isReservationNumberExist) {
            throw new ReservationNumberNotFoundException();
        }
    }

}
