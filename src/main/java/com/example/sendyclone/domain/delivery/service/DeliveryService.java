package com.example.sendyclone.domain.delivery.service;

import com.example.sendyclone.domain.delivery.exception.DeliveryErrorCode;
import com.example.sendyclone.domain.delivery.exception.DeliveryException;
import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import com.example.sendyclone.domain.delivery.model.DeliveryStatus;
import com.example.sendyclone.domain.delivery.model.dto.request.DeliverySaveRequest;
import com.example.sendyclone.domain.delivery.model.dto.request.DeliveryUpdateRequest;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliveryDetailResponse;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySaveResponse;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySummaryResponse;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySummaryResponseList;
import com.example.sendyclone.domain.delivery.model.entity.Delivery;
import com.example.sendyclone.domain.delivery.model.entity.StopOver;
import com.example.sendyclone.domain.delivery.repository.DeliveryRepository;
import com.example.sendyclone.domain.delivery.repository.StopOverRepository;
import com.example.sendyclone.domain.delivery.utils.ReservationNumberGenerator;
import com.example.sendyclone.domain.member.exception.MemberException;
import com.example.sendyclone.domain.member.model.entity.Member;
import com.example.sendyclone.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.sendyclone.domain.delivery.exception.DeliveryErrorCode.*;
import static com.example.sendyclone.domain.member.exception.MemberErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final MemberRepository memberRepository;
    private final StopOverRepository stopOverRepository;
    private final DeliveryCacheService deliveryCacheService;

    @Transactional // 데이터베이스에 unique 설정 + 체크 로직으로 동시성 문제 터지면 그냥 오류 발생 시키키 -> 저장할 때 동시성 문제는 거의 안 생기기 때문
    public DeliverySaveResponse saveDelivery(DeliverySaveRequest deliverySaveRequest, Long memberId) { //나중에 결제 추가하면 배달 상태도 추가해줘야 함
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberException.fromErrorCode(MEMBER_NOT_FOUND));

        final Delivery delivery = deliverySaveRequest.toDelivery();
        delivery.updateMember(member);
        delivery.updateDeliveryFee(10000); //결제금액은 프론트에서 결정

        String reservationNumber = ReservationNumberGenerator.withDate(deliveryRepository.count());
        if (deliveryRepository.existsByReservationNumber(reservationNumber)) {
            throw new DeliveryException(RESERVATION_NUMBER_DUPLICATED);
        }

        delivery.updateReservationNumber(reservationNumber);

        Delivery savedDelivery;
        try {
            savedDelivery = deliveryRepository.save(delivery);
        } catch (DataIntegrityViolationException e) { //unique 에러
            throw new DeliveryException(RESERVATION_NUMBER_DUPLICATED);
        }

        final List<DeliveryAddress> deliveryAddresses = saveStopOvers(deliverySaveRequest, savedDelivery);
        return DeliverySaveResponse.from(savedDelivery, deliveryAddresses);
    }

    @CacheEvict(value = "deliveryDetails", key = "#p0", cacheManager = "cacheManager")
    @Transactional
    public void deleteDelivery(String reservationNumber) {
        final Delivery delivery = deliveryRepository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> DeliveryException.fromErrorCode(DELIVERY_NOT_FOUND));

        List<StopOver> stopOverByDeliveryId = stopOverRepository.findStopOverByReservationNumber(reservationNumber);

        stopOverRepository.deleteAll(stopOverByDeliveryId);
        deliveryRepository.delete(delivery);
    }

    @CachePut(value = "deliveryDetails", key = "#p0", cacheManager = "cacheManager")
    @Transactional
    public DeliveryDetailResponse updateDelivery(String reservationNumber, DeliveryUpdateRequest deliveryUpdateRequest) { //경유지 변경감지 봐야함
        final Delivery delivery = deliveryRepository.findByReservationNumberWithPLock(reservationNumber)
                .orElseThrow(() -> DeliveryException.fromErrorCode(DELIVERY_NOT_FOUND));

            delivery.update(deliveryUpdateRequest);
        if (deliveryUpdateRequest.getDeliveryAddress() != null) {
            updateStopOver(reservationNumber, deliveryUpdateRequest, delivery);
        }

        return DeliveryDetailResponse.from(delivery, deliveryUpdateRequest.getStopOverAddresses());
    }

    @Cacheable(value = "deliveryDetails", key = "#p0", cacheManager = "cacheManager")
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

    // 배달리스트의 수정과 삭제가 빈번히 일어나기 때문에 전체 캐싱 X
    // 완료된 배달 부분 캐싱(수정, 삭제가 발생하지 않음)
    public List<DeliverySummaryResponse> getDeliveriesSummary(Long memberId) { //Page, Slice 코드 리팩토링 해야할수도
        validatedMemberExisted(memberId);

        final List<Delivery> deliveries = deliveryRepository.findDeliveriesByMemberId(memberId);
        if (deliveries == null) {
            throw new DeliveryException(DELIVERY_NOT_FOUND);
        }

        final List<Long> deliveryIds = deliveries.stream()
                .map(Delivery::getId)
                .toList();

        return createDeliverySummaryResponses(deliveryIds, deliveries);
    }

    public List<DeliverySummaryResponse> getDeliverySummariesWithCache (Long memberId) {
        validatedMemberExisted(memberId);

        List<DeliverySummaryResponse> completedDeliveriesSummary = deliveryCacheService.getCompletedDeliveriesSummary(memberId).getDeliverySummaryResponses();
        List<DeliverySummaryResponse> notCompletedDeliveriesSummary = getNotCompletedDeliveriesSummary(memberId);

        return Stream.concat(completedDeliveriesSummary.stream(), notCompletedDeliveriesSummary.stream())
                .toList();
    }

    public List<DeliverySummaryResponse> getNotCompletedDeliveriesSummary(Long memberId) {
        validatedMemberExisted(memberId);

        List<Delivery> deliveries = deliveryRepository.findNotCompletedDeliveriesByMemberId(memberId,
                List.of(DeliveryStatus.COMPLETED, DeliveryStatus.IN_PROGRESS, DeliveryStatus.DRAFT));
        if (deliveries == null) {
            throw new DeliveryException(DELIVERY_NOT_FOUND);
        }

        final List<Long> deliveryIds = deliveries.stream()
                .map(Delivery::getId)
                .toList();

        return createDeliverySummaryResponses(deliveryIds, deliveries);
    }

    private List<DeliverySummaryResponse> createDeliverySummaryResponses(List<Long> deliveryIds, List<Delivery> deliveries) {
        final List<StopOver> stopOverByDeliveryIds = stopOverRepository.findStopOverByDeliveryIds(deliveryIds);
        if (stopOverByDeliveryIds != null && !stopOverByDeliveryIds.isEmpty()) {
            final Map<Long, List<StopOver>> stopOversByDeliveryId = stopOverByDeliveryIds.stream()
                    .collect(Collectors.groupingBy(stopOver -> stopOver.getDelivery().getId()));

            return deliveries.stream()
                    .map(delivery -> {
                        int size = stopOversByDeliveryId.get(delivery.getId()).size();
                        return DeliverySummaryResponse.from(delivery, size);
                    })
                    .toList();
        }

        return deliveries.stream()
                .map(delivery -> DeliverySummaryResponse.from(delivery, 0))
                .toList();
    }

    private void updateStopOver(String reservationNumber, DeliveryUpdateRequest deliveryUpdateRequest, Delivery delivery) {
        List<DeliveryAddress> stopOverAddresses = deliveryUpdateRequest.getStopOverAddresses();
        List<StopOver> stopOverByDeliveryId = stopOverRepository.findStopOverByReservationNumber(reservationNumber);
        if (stopOverByDeliveryId == null) {
            DeliveryException.fromErrorCode(STOPOVER_NOT_FOUND);
        }

        List<StopOver> stopOversToDelete = findStopOversToDelete(stopOverByDeliveryId, stopOverAddresses);
        List<DeliveryAddress> stopOversToAdd = findDeliveryAddressToAdd(stopOverAddresses, stopOverByDeliveryId);

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
