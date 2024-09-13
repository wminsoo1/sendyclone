package com.example.sendyclone.domain.delivery.model.entity;

import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import com.example.sendyclone.domain.delivery.model.DeliveryCategory;
import com.example.sendyclone.domain.delivery.model.DeliveryStatus;
import com.example.sendyclone.domain.driver.model.Driver;
import com.example.sendyclone.domain.driver.model.Vehicle;
import com.example.sendyclone.domain.member.model.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Enumerated(value = EnumType.STRING)
    private DeliveryStatus deliveryStatus; //결제 추가하면 상태도 추가 해줘야 함

    @Column(nullable = false)
    private String reservationNumber;

    @Embedded
    @Column(nullable = false)
    private DeliveryCategory deliveryCategory;

    @Column(nullable = false)
    private LocalDateTime deliveryDate;

    @Embedded
    @Column(nullable = false)
    private Vehicle vehicle;

    @Embedded
    @Column(nullable = false)
    private DeliveryAddress deliveryAddress;

    @Column(nullable = false)
    private double deliveryFee; //BigDemical 프론트에서 처리하는거 같음

    private String deliveryOptions;

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updateDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void updateReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    private Delivery(Long id, Member member, Driver driver, DeliveryStatus deliveryStatus, String reservationNumber, DeliveryCategory deliveryCategory, LocalDateTime deliveryDate, Vehicle vehicle, DeliveryAddress deliveryAddress, double deliveryFee, String deliveryOptions) {
        this.id = id;
        this.member = member;
        this.driver = driver;
        this.deliveryStatus = deliveryStatus;
        this.reservationNumber = reservationNumber;
        this.deliveryCategory = deliveryCategory;
        this.deliveryDate = deliveryDate;
        this.vehicle = vehicle;
        this.deliveryAddress = deliveryAddress;
        this.deliveryFee = deliveryFee;
        this.deliveryOptions = deliveryOptions;
    }
}
