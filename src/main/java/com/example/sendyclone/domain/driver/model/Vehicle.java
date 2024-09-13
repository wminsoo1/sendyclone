package com.example.sendyclone.domain.driver.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Getter
@Embeddable
public class Vehicle {

    @Enumerated(value = EnumType.STRING)
    private VehicleWeight vehicleWeight;

    @Enumerated(value = EnumType.STRING)
    private VehicleType vehicleType;
}
