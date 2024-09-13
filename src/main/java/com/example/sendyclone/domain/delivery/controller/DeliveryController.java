package com.example.sendyclone.domain.delivery.controller;

import com.example.sendyclone.domain.delivery.model.dto.request.DeliverySaveRequest;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliveryDetailResponse;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySaveResponse;
import com.example.sendyclone.domain.delivery.model.dto.response.DeliverySummaryResponse;
import com.example.sendyclone.domain.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/delivery")
    public ResponseEntity<DeliverySaveResponse> saveDelivery(
            @RequestHeader("memberId") Long memberId, //Spring Security 추가하면 변경!
            @Valid @RequestBody DeliverySaveRequest deliverySaveRequest) {
        final DeliverySaveResponse deliverySaveResponse = deliveryService.saveDelivery(deliverySaveRequest, memberId);

        return ResponseEntity.ok().body(deliverySaveResponse);
    }

    @GetMapping("/deliveryDetail")
    public ResponseEntity<DeliveryDetailResponse> getDeliveryDetail(
            @RequestHeader("memberId") Long memberId, //Spring Security 추가하면 변경!
            @RequestParam("reservationNumber") String reservationNumber) {
        final DeliveryDetailResponse deliveryDetail = deliveryService.getDeliveryDetail(reservationNumber, memberId);

        return ResponseEntity.ok().body(deliveryDetail);
    }

    @GetMapping("/deliverys")
    public ResponseEntity<List<DeliverySummaryResponse>> getDeliveriesSummary(
            @RequestHeader("memberId") Long memberId) {
        final List<DeliverySummaryResponse> deliveriesSummary = deliveryService.getDeliveriesSummary(memberId);

        return ResponseEntity.ok().body(deliveriesSummary);
    }

}
