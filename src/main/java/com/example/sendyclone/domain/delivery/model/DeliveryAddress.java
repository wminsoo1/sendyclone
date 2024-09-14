package com.example.sendyclone.domain.delivery.model;

import com.example.sendyclone.domain.delivery.exception.DeliveryErrorCode;
import com.example.sendyclone.domain.delivery.exception.DeliveryException;
import com.example.sendyclone.domain.delivery.model.entity.StopOver;
import jakarta.persistence.Embeddable;
import jakarta.websocket.DecodeException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.example.sendyclone.domain.delivery.exception.DeliveryErrorCode.*;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddress {
    private String pickupLocation;
    private String dropLocation;

    private DeliveryAddress(String pickupLocation, String dropLocation) {
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
    }

    public DeliveryAddress update(DeliveryAddress deliveryAddress) {
        validateDeliveryExist(deliveryAddress);
        this.pickupLocation = deliveryAddress.getPickupLocation();
        this.dropLocation = deliveryAddress.getDropLocation();

        return new DeliveryAddress(pickupLocation, dropLocation);
        //방어적 복사 일단 하긴 했는데 dto에 처음 만들 때만 값을 세팅하고 그 이후로 set을 제공하지 않으면 방어적 복사를 할 필요가 있나?
        //실제로 dto에 메서드로 빌더패턴만 제공하고 set 메서드는 제공하지 않음
    }

    private void validateDeliveryExist(DeliveryAddress deliveryAddress) {
        if (deliveryAddress == null) {
            throw new DeliveryException(DELIVERY_ADDRESS_NOT_FOUND);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DeliveryAddress address = (DeliveryAddress) object;
        return Objects.equals(pickupLocation, address.pickupLocation) && Objects.equals(dropLocation, address.dropLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pickupLocation, dropLocation);
    }
}
