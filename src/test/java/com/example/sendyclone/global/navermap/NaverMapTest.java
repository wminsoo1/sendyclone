package com.example.sendyclone.global.navermap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NaverMapTest {

    @Autowired
    NaverMap naverMap;

    @Test
    void test() {
        long fee = naverMap.getFee("금정구 장전온천천로 48", "부산광역시 금곡대로 166");
        System.out.println("fee = " + fee);
    }
}