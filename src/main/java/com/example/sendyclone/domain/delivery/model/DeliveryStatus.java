package com.example.sendyclone.domain.delivery.model;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    DRAFT("작성중"),
    COMPLETED("작성완료"),
    IN_PROGRESS("배달진행중"),
    DELIVERED("배달완료");

    private final String description;

    DeliveryStatus(String description) {
        this.description = description;
    }
}
