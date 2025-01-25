package com.txzmap.spliceservice.entity;

import lombok.Data;

@Data
public class BoundingBox {
    double north, south, east, west;

    /**
     * 获取西北角的经纬度
     */
    public GeoPos getNorthWest() {
        return new GeoPos(north, west);
    }

    /**
     * 获取东北角的经纬度
     */
    public GeoPos getNorthEast() {
        return new GeoPos(north, east);
    }

    /**
     * 获取东南角的经纬度
     */
    public GeoPos getSouthEast() {
        return new GeoPos(south, east);
    }

    /**
     * 获取西南角的经纬度
     */
    public GeoPos getSouthWest() {
        return new GeoPos(south, west);
    }

    /**
     * 打印边界框的四个角的经纬度
     */
    public void printCorners() {
        System.out.println("西北角: " + getNorthWest());
        System.out.println("东北角: " + getNorthEast());
        System.out.println("东南角: " + getSouthEast());
        System.out.println("西南角: " + getSouthWest());
    }
}
