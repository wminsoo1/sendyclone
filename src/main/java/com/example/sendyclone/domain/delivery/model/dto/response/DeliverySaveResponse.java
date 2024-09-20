package com.example.sendyclone.domain.delivery.model.dto.response;

import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import com.example.sendyclone.domain.delivery.model.DeliveryCategory;
import com.example.sendyclone.domain.delivery.model.entity.Delivery;
import com.example.sendyclone.domain.driver.model.Vehicle;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliverySaveResponse {

    private Long memberId;

    private String reservationNumber;

    @Embedded
    private DeliveryCategory deliveryCategory;

    private LocalDateTime deliveryDate;

    @Embedded
    private Vehicle vehicle;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Embedded
    private List<DeliveryAddress> stopOverAddresses;

    private String deliveryOptions;

    private double deliveryFee; //BigDemical

    private DeliverySaveResponse(Long memberId, String reservationNumber, DeliveryCategory deliveryCategory, LocalDateTime deliveryDate, Vehicle vehicle, DeliveryAddress deliveryAddress, List<DeliveryAddress> stopOverAddresses, String deliveryOptions, double deliveryFee) {
        this.memberId = memberId;
        this.reservationNumber = reservationNumber;
        this.deliveryCategory = deliveryCategory;
        this.deliveryDate = deliveryDate;
        this.vehicle = vehicle;
        this.deliveryAddress = deliveryAddress;
        this.stopOverAddresses = stopOverAddresses;
        this.deliveryOptions = deliveryOptions;
        this.deliveryFee = deliveryFee;
    }

    public static DeliverySaveResponse from(final Delivery delivery, final List<DeliveryAddress> stopOverAddresses) {
        return new DeliverySaveResponse(
                delivery.getMember().getId(),
                delivery.getReservationNumber(),
                delivery.getDeliveryCategory(),
                delivery.getDeliveryDate(),
                delivery.getVehicle(),
                delivery.getDeliveryAddress(),
                stopOverAddresses,
                delivery.getDeliveryOptions(),
                delivery.getDeliveryFee());
    }

    @Override
    public String toString() {
        return "DeliverySaveResponse{" +
                "memberId=" + memberId +
                ", reservationNumber='" + reservationNumber + '\'' +
                ", deliveryCategory=" + deliveryCategory +
                ", deliveryDate=" + deliveryDate +
                ", vehicle=" + vehicle +
                ", deliveryAddress=" + deliveryAddress +
                ", stopOverAddresses=" + stopOverAddresses +
                ", deliveryOptions='" + deliveryOptions + '\'' +
                ", deliveryFee=" + deliveryFee +
                '}';
    }
}
