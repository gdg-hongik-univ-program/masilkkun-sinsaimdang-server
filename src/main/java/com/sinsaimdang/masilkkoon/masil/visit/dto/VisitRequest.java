package com.sinsaimdang.masilkkoon.masil.visit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class VisitRequest {

    @JsonProperty("road_address")
    private RoadAddress roadAddress;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @ToString
    public static class RoadAddress {

        @JsonProperty("address_name")
        private String addressName;
        // 특별시 / 광역시 / 특별자치시 / 6도 / 특별자치도
        @JsonProperty("region_1depth_name")
        private String region1DepthName;

        // 시 / 군 / 구
        @JsonProperty("region_2depth_name")
        private String region2DepthName;

    }

    public String getFullAddressName() {
        if (roadAddress != null) {
            return roadAddress.getAddressName();
        }
        return null;
    }
    public String getRegion1DepthName() {
        if (roadAddress != null) {
            return roadAddress.getRegion1DepthName();
        }
        return null;
    }

    public String getRegion2DepthName() {
        if (roadAddress != null) {
            return roadAddress.getRegion2DepthName();
        }
        return null;
    }
}
