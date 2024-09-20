package com.example.sendyclone.domain.delivery.service;

import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import com.example.sendyclone.domain.delivery.model.DeliveryCategory;
import com.example.sendyclone.domain.delivery.model.DeliveryType;
import com.example.sendyclone.domain.delivery.model.PersonalDeliveryCategory;
import com.example.sendyclone.domain.delivery.model.dto.request.DeliverySaveRequest;
import com.example.sendyclone.domain.delivery.model.dto.request.DeliveryUpdateRequest;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySaveResponse;
import com.example.sendyclone.domain.delivery.repository.DeliveryRepository;
import com.example.sendyclone.domain.driver.model.Vehicle;
import com.example.sendyclone.domain.driver.model.VehicleType;
import com.example.sendyclone.domain.driver.model.VehicleWeight;
import com.example.sendyclone.domain.member.model.entity.Member;
import com.example.sendyclone.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class DeliveryServiceIntegrationTest {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("한 멤버가 배달을 동시에 저장 - 경유지 없음")
    public void saveDeliveryConcurrentWhenNOStopOvers() throws InterruptedException, ExecutionException {
        // given
        createMembers(1);

        Long memberId = 1L;
        DeliverySaveRequest deliverySaveRequest = createDummyDeliverySaveRequestWithoutStopOvers();

        //when
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Callable<DeliverySaveResponse>> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> deliveryService.saveDelivery(deliverySaveRequest, memberId));
        }

        List<Future<DeliverySaveResponse>> futures = executorService.invokeAll(tasks);

        for (Future<DeliverySaveResponse> future : futures) {
            DeliverySaveResponse deliverySaveResponse = future.get();
            System.out.println("deliverySaveResponse = " + deliverySaveResponse);
        }

        executorService.shutdown();
    }

    @Test
    @DisplayName("배달 동시 업데이트")
    public void updateDeliveryConcurrentWhenNOStopOvers() throws InterruptedException {
        // given
        Long deliveryId = 1L;

        //when
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            DeliveryUpdateRequest deliveryUpdateRequest = createDeliveryUpdateRequest(i);
            tasks.add(() -> {
                deliveryService.updateDelivery(deliveryId, deliveryUpdateRequest);
                return null;
            });
        }

        executorService.invokeAll(tasks);

        executorService.shutdown();
    }

    private void createMembers(int count) {
        for (int i = 1; i <= count; i++) {
            Member member = Member.builder()
                    .name("member" + i)
                    .email("member" + i + "@example.com")
                    .password("password" + i)
                    .build();
            memberRepository.saveAndFlush(member);
        }
    }

    private DeliveryUpdateRequest createDeliveryUpdateRequest(int i) {
        DeliveryAddress deliveryAddress = new DeliveryAddress("부산대역", "장전역");
        Vehicle vehicle = new Vehicle(VehicleWeight.DAMAS, VehicleType.FREEZER);
        LocalDateTime deliveryDate = LocalDateTime.of(2024, 9, 12, 14, 30);
        String deliveryOptions = "문 앞에 놓아주세요" + i;
        List<DeliveryAddress> stopOverAddresses = Collections.emptyList(); // 경유지가 없음

        return new DeliveryUpdateRequest(
                deliveryAddress,
                vehicle,
                deliveryDate,
                deliveryOptions,
                stopOverAddresses
        );
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
}