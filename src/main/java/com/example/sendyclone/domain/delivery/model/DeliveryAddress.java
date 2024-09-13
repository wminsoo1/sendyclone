package com.example.sendyclone.domain.delivery.model;

import com.example.sendyclone.domain.delivery.model.entity.StopOver;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class DeliveryAddress {
    private String pickupLocation;
    private String dropLocation;

    protected DeliveryAddress() {
    }

}
