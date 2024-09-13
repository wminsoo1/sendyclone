package com.example.sendyclone.domain.delivery.model.dto.response;

import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import com.example.sendyclone.domain.delivery.model.entity.Delivery;
import com.example.sendyclone.domain.delivery.model.entity.StopOver;
import com.example.sendyclone.domain.driver.model.Vehicle;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliverySummaryResponse {

    private String reservationNumber;

    private LocalDateTime deliveryDate;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Embedded
    private Vehicle vehicle;

    private Integer stopOverCount;

    private String deliveryOptions;

    private double deliveryFee; //BigDemical

    private DeliverySummaryResponse(String reservationNumber, LocalDateTime deliveryDate, DeliveryAddress deliveryAddress, Vehicle vehicle, Integer stopOverCount, String deliveryOptions, double deliveryFee) {
        this.reservationNumber = reservationNumber;
        this.deliveryDate = deliveryDate;
        this.deliveryAddress = deliveryAddress;
        this.vehicle = vehicle;
        this.stopOverCount = stopOverCount;
        this.deliveryOptions = deliveryOptions;
        this.deliveryFee = deliveryFee;
    }

    public static DeliverySummaryResponse from(Delivery delivery, Integer stopOverCount) {
        return new DeliverySummaryResponse(
                delivery.getReservationNumber(),
                delivery.getDeliveryDate(),
                delivery.getDeliveryAddress(),
                delivery.getVehicle(),
                stopOverCount,
                delivery.getDeliveryOptions(),
                delivery.getDeliveryFee()
        );
    }
}
