package com.example.sendyclone.domain.delivery.model.entity;

import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StopOver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private StopOver(Long id, DeliveryAddress deliveryAddress, Delivery delivery) {
        this.id = id;
        this.deliveryAddress = deliveryAddress;
        this.delivery = delivery;
    }

    public void updateDelivery(Delivery delivery) {
        this.delivery = delivery;
    }
}
