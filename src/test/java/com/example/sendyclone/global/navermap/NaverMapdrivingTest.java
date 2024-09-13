package com.example.sendyclone.global.navermap;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NaverMapdrivingTest {

    @Autowired
    private NaverMapDriving naverMapdriving;

    @Test
    void 응답test() {
        String distance = naverMapdriving.getDistance("127.1058342,37.359708", "129.075986,35.179470");
        System.out.println("distance = " + distance);
    }

}