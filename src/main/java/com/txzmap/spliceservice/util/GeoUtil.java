package com.txzmap.spliceservice.util;

import com.txzmap.spliceservice.entity.GeoPos;
import org.osgeo.proj4j.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class GeoUtil {
    private static final int WIDE_3 = 0, WIDE_6 = 1;
    private static final DecimalFormat df = new DecimalFormat("###0");
    public static final DecimalFormat df_y = new DecimalFormat("000000");
    public static final DecimalFormat df_x = new DecimalFormat("0000000");
    public static final DecimalFormat df_latlon = new DecimalFormat("###.00");

    private static CRSFactory crsFactory = new CRSFactory();
    private static CoordinateReferenceSystem WGS84 = crsFactory.createFromParameters("wgs84", "+proj=longlat  +no_defs ");

    private static CoordinateReferenceSystem CGCS2000 = crsFactory.createFromParameters("cgcs2000", "+proj=tmerc +lat_0=0  +k=1 +x_0=500000 +y_0=0 +ellps=GRS80 +units=m +no_defs");

    /**
     * 度向度分秒的转换
     *
     * @param p
     * @return
     */
    public static String d2DFM(double p) {
        int d, f;
        d = (int) p;
        int t = (int) ((p - d) * 3600);
        f = (int) (t / 60);
        double m = (p - d) * 3600f - f * 60f;
        return d + "°" + f + "′" + df.format(m) + "″";
    }


    /**
     * 根据经度来求所在的中央经线的带号
     *
     * @param lon
     * @param type
     * @return
     */
    private static int getZoneNumber(Double lon, int type) {
        if (type == WIDE_3) {
            return (int) Math.floor((lon + 1.5d) / 3.);
        }

        if (type == WIDE_6) {
            return (int) Math.floor((lon + 6.0d) / 6.);
        }
        return 0;
    }

    /**
     * 根据带号获取中央经线
     *
     * @param zoneNo
     * @param type
     * @return
     */
    private static double getCentreLon(int zoneNo, int type) {
        if (type == WIDE_6) {
            return (zoneNo - 1) * 6 + 3;
        } else
            return zoneNo * 3;
    }

    private static double getCentreLon(double lon, int type) {
        return getCentreLon(getZoneNumber(lon, type), type);
    }

    public static double[] WGS84ToCGCS2000(double lat, double lon) {

        double lon0 = getCentreLon(lon, WIDE_6);
        //设置中央经纬
        CGCS2000.getProjection().setProjectionLongitudeDegrees(lon0);
        //定义转换类
        CoordinateTransformFactory ctf = new CoordinateTransformFactory();
        CoordinateTransform transform = ctf.createTransform(WGS84, CGCS2000);

        //WGS84坐标系转换
        ProjCoordinate projCoordinate = new ProjCoordinate(lon, lat);
        transform.transform(projCoordinate, projCoordinate);


        System.out.println(projCoordinate.toShortString() + "/" + lon0);

        return new double[]{projCoordinate.x, projCoordinate.y};
    }

    public static double[] WGS84ToCGCS2000(GeoPos latlon) {
        return WGS84ToCGCS2000(latlon.getLat(), latlon.getLng());
    }

    public static void main(String[] args) throws IOException {
        String path = "/home/jayhuang/桌面/1652010388976.jpg";
        BufferedImage image = ImageIO.read(new File(path));

        Graphics2D graphics2D = image.createGraphics();
        Font font = new Font("Arial", Font.BOLD, 16);
        graphics2D.setFont(font);
        graphics2D.setStroke(new BasicStroke(1.0f));
        //两个角的高斯坐标
        double[] xy1 = GeoUtil.WGS84ToCGCS2000(new GeoPos(23.119601, 114.346304));
        double[] xy2 = GeoUtil.WGS84ToCGCS2000(new GeoPos(23.058963, 114.434452));
        double x1 = xy1[1];
        double x2 = xy2[1];
        double y1 = xy1[0];
        double y2 = xy2[0];


        double xSpan = x1 - x2;
        double ySpan = y2 - y1;
        int width = image.getWidth();
        int height = image.getHeight();

        double xPerPixel = height / xSpan;
        double yPerPixel = width / ySpan;


        double x0 = Math.ceil(x2 / 1000) * 1000;
        String lable = "";
        if (x0 < x1) {
            for (int i = 0; (height - (x0 + i * 1000 - x2) * xPerPixel) > 0; i++) {
                int pt = (int) (height - (x0 + i * 1000 - x2) * xPerPixel);
                graphics2D.drawLine(0, pt, width, pt);
                lable = df_x.format(x0 + i * 1000);
                if (i == 0)
                    graphics2D.drawString(lable, 0, pt);
                else
                    graphics2D.drawString(lable.substring(2, 4), 0, pt);

            }
        }
        double y0 = Math.ceil(y1 / 1000) * 1000;
        if (y0 < y2) {
            for (int i = 0; (width - (y0 + i * 1000 - y1) * yPerPixel) > 0; i++) {
                int pt = (int) ((y0 + i * 1000 - y1) * yPerPixel);
                graphics2D.drawLine(pt, 0, pt, height);
                lable = df_y.format(y0 + i * 1000);
                if (i == 0)
                    graphics2D.drawString(lable, pt, 0 + 20);
                else
                    graphics2D.drawString(lable.substring(1, 3), pt, 0 + 20);
            }
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File outFile = new File("/home/jayhuang/桌面/" + fileName);
        try {
            ImageIO.write(image, "jpg", outFile);

        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
