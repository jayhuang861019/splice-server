package com.txzmap.spliceservice.util;

import java.io.IOException;

/**
 * 瓦片坐标和经纬度坐标的转换公式
 */
public class TileUtil {
    public static class BoundingBox {
        public double north, south, east, west;

        public double[] getLeftTop() {
            return new double[]{north, west};
        }

        public double[] getRightBottom() {
            return new double[]{south, east};
        }

        @Override
        public String toString() {
            return "BoundingBox{" +
                    "north=" + north +
                    ", south=" + south +
                    ", east=" + east +
                    ", west=" + west +
                    '}';
        }
    }

    /**
     * 根据经纬度获取其所在瓦片的编号
     *
     * @param lat
     * @param lon
     * @param zoom
     * @return
     */
    public static String getTileNumber(final double lat, final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return ("" + zoom + "/" + xtile + "/" + ytile);
    }


    /**
     * 根据瓦片计算其经纬度范围
     *
     * @param x
     * @param y
     * @param zoom
     * @return
     */
    public static BoundingBox tile2boundingBox(final int x, final int y, final int zoom) {
        BoundingBox bb = new BoundingBox();
        bb.north = tile2lat(y, zoom);
        bb.south = tile2lat(y + 1, zoom);
        bb.west = tile2lon(x, zoom);
        bb.east = tile2lon(x + 1, zoom);
        return bb;
    }

    static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }



    public static void main(String[] args) throws IOException {

    }

}
