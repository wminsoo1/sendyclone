package com.example.sendyclone.domain.delivery.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationNumberGenerator {

    private ReservationNumberGenerator() {
    }

    public static String withDate(long deliveryCount) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        return now.format(formatter) + "-" + deliveryCount;
    }

}
