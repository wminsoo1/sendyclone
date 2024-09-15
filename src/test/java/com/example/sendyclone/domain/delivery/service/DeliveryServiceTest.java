package com.example.sendyclone.domain.delivery.service;

import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import com.example.sendyclone.domain.delivery.model.DeliveryCategory;
import com.example.sendyclone.domain.delivery.model.DeliveryType;
import com.example.sendyclone.domain.delivery.model.PersonalDeliveryCategory;
import com.example.sendyclone.domain.delivery.model.dto.request.DeliverySaveRequest;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySaveResponse;
import com.example.sendyclone.domain.delivery.model.entity.Delivery;
import com.example.sendyclone.domain.delivery.model.entity.StopOver;
import com.example.sendyclone.domain.delivery.repository.DeliveryRepository;
import com.example.sendyclone.domain.delivery.repository.StopOverRepository;
import com.example.sendyclone.domain.driver.model.Vehicle;
import com.example.sendyclone.domain.driver.model.VehicleType;
import com.example.sendyclone.domain.driver.model.VehicleWeight;
import com.example.sendyclone.domain.member.exception.MemberErrorCode;
import com.example.sendyclone.domain.member.exception.MemberException;
import com.example.sendyclone.domain.member.model.entity.Member;
import com.example.sendyclone.domain.member.repository.MemberRepository;
import org.awaitility.core.DeadlockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.sendyclone.domain.member.exception.MemberErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @InjectMocks
    DeliveryService deliveryService;

    @Mock
    DeliveryRepository deliveryRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    StopOverRepository stopOverRepository;

    @Test
    @DisplayName("회원 ID가 존재하지 않으면 예외 발생")
    void saveDeliveryThrowsExceptionWhenMemberNotFound() {
        //given
        Long invalidMemberId = 999L;

        given(memberRepository.findById(invalidMemberId)).willReturn(Optional.empty());

        //when
        assertThrows(MemberException.class,() -> deliveryService.saveDelivery(null, invalidMemberId));
        verify(memberRepository).findById(invalidMemberId);
    }

    @Test
    @DisplayName("경유지가 없을 때 배달 저장")
    void saveDeliveryWhenNoStopOvers() {
        //given
        Long validMemberId = 1L;
        DeliverySaveRequest deliverySaveRequest = createDummyDeliverySaveRequestWithoutStopOvers();
        Delivery delivery = deliverySaveRequest.toDelivery();
        Member member = Member.builder()
                .id(validMemberId)
                .name("aaa")
                .email("aaa@nabver.com")
                .password("aaaaaaa!")
                .build();

        given(memberRepository.findById(validMemberId)).willReturn(Optional.ofNullable(member));
        given(deliveryRepository.save(any(Delivery.class))).willReturn(delivery);

        //when
        DeliverySaveResponse deliverySaveResponse = deliveryService.saveDelivery(deliverySaveRequest, validMemberId);

        //then
        assertNotNull(deliverySaveResponse);
        verify(memberRepository).findById(validMemberId);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    @DisplayName("경유지가 있을 때 배달 저장")
    void saveDeliveryWhenStopOvers() {
        //given
        Long validMemberId = 1L;
        DeliverySaveRequest deliverySaveRequest = createDummyDeliverySaveRequestWithStopOvers();
        Delivery delivery = deliverySaveRequest.toDelivery();
        StopOver stopOver = StopOver.builder()
                .delivery(delivery)
                .deliveryAddress(new DeliveryAddress("중간 정거장1", "중간 정거장2"))
                .build();
        Member member = Member.builder()
                .id(validMemberId)
                .name("aaa")
                .email("aaa@nabver.com")
                .password("aaaaaaa!")
                .build();

        given(memberRepository.findById(validMemberId)).willReturn(Optional.ofNullable(member));
        given(deliveryRepository.save(any(Delivery.class))).willReturn(delivery);
        given(stopOverRepository.save(any(StopOver.class))).willReturn(stopOver);

        //when
        DeliverySaveResponse deliverySaveResponse = deliveryService.saveDelivery(deliverySaveRequest, validMemberId);

        //then
        assertNotNull(deliverySaveResponse);
        verify(memberRepository).findById(validMemberId);
        verify(deliveryRepository).save(any(Delivery.class));
        verify(stopOverRepository).save(any(StopOver.class));
    }




    private DeliverySaveRequest createDummyDeliverySaveRequestWithoutStopOvers() {
        DeliveryCategory deliveryCategory = new DeliveryCategory(DeliveryType.PERSONAL, PersonalDeliveryCategory.PERSONAL_GENERAL_CARGO);
        LocalDateTime deliveryDate = LocalDateTime.of(2024, 9, 12, 14, 30);
        Vehicle vehicle = new Vehicle(VehicleWeight.DAMAS, VehicleType.FREEZER);
        DeliveryAddress deliveryAddress = new DeliveryAddress("부산대역", "장전역");
        List<DeliveryAddress> stopOverAddresses = Collections.emptyList(); // 경유지가 없음
        String deliveryOptions = "문 앞에 놓아주세요";

        return new DeliverySaveRequest(
                deliveryCategory,
                deliveryDate,
                vehicle,
                deliveryAddress,
                stopOverAddresses,
                deliveryOptions
        );
    }

    private DeliverySaveRequest createDummyDeliverySaveRequestWithStopOvers() {
        DeliveryCategory deliveryCategory = new DeliveryCategory(DeliveryType.PERSONAL, PersonalDeliveryCategory.PERSONAL_GENERAL_CARGO);
        LocalDateTime deliveryDate = LocalDateTime.of(2024, 9, 12, 14, 30);
        Vehicle vehicle = new Vehicle(VehicleWeight.DAMAS, VehicleType.FREEZER);
        DeliveryAddress deliveryAddress = new DeliveryAddress("부산대역", "장전역");
        List<DeliveryAddress> stopOverAddresses = Arrays.asList(
                new DeliveryAddress("중간 정거장1", "중간 정거장2")
        );
        String deliveryOptions = "문 앞에 놓아주세요";

        return new DeliverySaveRequest(
                deliveryCategory,
                deliveryDate,
                vehicle,
                deliveryAddress,
                stopOverAddresses,
                deliveryOptions
        );
    }
}