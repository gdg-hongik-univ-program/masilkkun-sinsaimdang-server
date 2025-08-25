package com.sinsaimdang.masilkkoon.masil.visit.dto;

import lombok.Getter;

@Getter
public class MapStampResponse {
    private final String region;
    private final int colorLevel;

    public MapStampResponse(String regionName, int colorLevel) {
        this.region = regionName;
        this.colorLevel = colorLevel;
    }
}
