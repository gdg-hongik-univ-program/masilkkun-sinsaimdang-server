package com.sinsaimdang.masilkkoon.masil.visit.dto;

import com.sinsaimdang.masilkkoon.masil.region.entity.Region;
import lombok.Getter;

@Getter
public class MapStampResponse {
    private final String region;
    private final int color;

    public MapStampResponse(Region region, int visitCount) {
        this.region = region.getName();
        this.color = visitCount;
    }
}
