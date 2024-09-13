package com.example.sendyclone.global.navermap;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NaverMapGeocodeTest {

    @Autowired
    NaverMapGeocode naverMapGeocode;

    @Test
    void 응답test() {
        JsonNode 부산대역 = naverMapGeocode.getAddressInfo("금정구 장전온천천로 48");
        System.out.println("부산대역 = " + 부산대역);

        JsonNode home = naverMapGeocode.getAddressInfo("부산광역시 금곡대로 166");
        System.out.println("집 = " + home);
    }
}