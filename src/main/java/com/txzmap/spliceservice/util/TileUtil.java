package com.txzmap.spliceservice.util;

import com.txzmap.spliceservice.entity.BoundingBox;
import com.txzmap.spliceservice.entity.GeoPos;

import java.io.IOException;

/**
 * 瓦片坐标和经纬度坐标的转换公式
 */
public class TileUtil {
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


    public static int[] getTileXY(final double lat, final double lon, final int zoom) {
        int xTile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int yTile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xTile < 0)
            xTile = 0;
        if (xTile >= (1 << zoom))
            xTile = ((1 << zoom) - 1);
        if (yTile < 0)
            yTile = 0;
        if (yTile >= (1 << zoom))
            yTile = ((1 << zoom) - 1);
        int[] res={xTile,yTile};
        return res;
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
        bb.setNorth(tile2lat(y, zoom));
        bb.setSouth(tile2lat(y + 1, zoom));
        bb.setWest( tile2lon(x, zoom));
        bb.setEast(tile2lon(x + 1, zoom));
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
int maxX=26987,maxY=12418,minX=26974,minY=12411,zoom=15;
        GeoPos p1 = TileUtil.tile2boundingBox(minX, minY, zoom).getNorthWest();
        GeoPos p2 = TileUtil.tile2boundingBox(maxX, minY, zoom).getNorthEast();
        GeoPos p3 = TileUtil.tile2boundingBox(maxX, maxY, zoom).getSouthEast();
        GeoPos p4 = TileUtil.tile2boundingBox(minX, maxY, zoom).getSouthWest();

        //4个角的高斯坐标
        double[] xy1 = GeoUtil.WGS84ToCGCS2000(p1);
        double[] xy2 = GeoUtil.WGS84ToCGCS2000(p2);
        double[] xy3 = GeoUtil.WGS84ToCGCS2000(p3);
        double[] xy4 = GeoUtil.WGS84ToCGCS2000(p4);


    }

}
