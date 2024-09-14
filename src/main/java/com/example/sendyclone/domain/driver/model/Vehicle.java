package com.example.sendyclone.domain.driver.model;

import com.example.sendyclone.domain.delivery.exception.DeliveryException;
import com.example.sendyclone.domain.delivery.model.DeliveryAddress;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.sendyclone.domain.delivery.exception.DeliveryErrorCode.DELIVERY_ADDRESS_NOT_FOUND;
import static com.example.sendyclone.domain.delivery.exception.DeliveryErrorCode.VEHICLE_NOT_FOUND;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle {

    @Enumerated(value = EnumType.STRING)
    private VehicleWeight vehicleWeight;

    @Enumerated(value = EnumType.STRING)
    private VehicleType vehicleType;

    private Vehicle(Vehicle vehicle) {
        this.vehicleWeight = vehicle.getVehicleWeight();
        this.vehicleType = vehicle.getVehicleType();
    }

    public Vehicle update(Vehicle vehicle) {
        validateVehicleExist(vehicle);
        this.vehicleWeight = vehicle.getVehicleWeight();
        this.vehicleType = vehicle.getVehicleType();

        return new Vehicle(vehicle);
    }

    private void validateVehicleExist(Vehicle vehicle) {
        if (vehicle == null) {
            throw new DeliveryException(VEHICLE_NOT_FOUND);
        }
    }
}
