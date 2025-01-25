package com.txzmap.spliceservice.entity;

import lombok.Data;

@Data
public class GeoPos {
    Double lat;
    Double lng;

    public GeoPos(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public GeoPos(){

    }

}
