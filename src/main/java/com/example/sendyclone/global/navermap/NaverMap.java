package com.example.sendyclone.global.navermap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverMap {

    private final NaverMapDriving naverMapDriving;
    private final NaverMapGeocode naverMapGeocode;

    public long getFee(String start, String goal) {
        JsonNode startAddress = naverMapGeocode.getAddressInfo(start).path("addresses").get(0);
        JsonNode goalAddress = naverMapGeocode.getAddressInfo(goal).path("addresses").get(0);

        String startX = startAddress.path("x").asText();
        String startY = startAddress.path("y").asText();

        String goalX = goalAddress.path("x").asText();
        String goalY = goalAddress.path("y").asText();

        String distanceInfo = naverMapDriving.getDistance(startX + "," + startY, goalX + "," + goalY);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode distanceJsonNode;
        try {
            distanceJsonNode = objectMapper.readTree(distanceInfo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse distance info JSON", e);
        }

        // taxiFare 값을 추출
        JsonNode traoptimalArray = distanceJsonNode.path("route").path("traoptimal");
        System.out.println("traoptimalArray = " + traoptimalArray);
        JsonNode summary = traoptimalArray.get(0).path("summary");
        long taxiFare = summary.path("taxiFare").asLong();

        return taxiFare;
    }
}
