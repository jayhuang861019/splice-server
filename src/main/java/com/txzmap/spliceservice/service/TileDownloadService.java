package com.txzmap.spliceservice.service;

import com.txzmap.spliceservice.config.MyConfig;
import com.txzmap.spliceservice.entity.GeoPos;
import com.txzmap.spliceservice.entity.MapSource;
import com.txzmap.spliceservice.entity.TaskInfoList;
import com.txzmap.spliceservice.entity.TbTile;
import com.txzmap.spliceservice.util.GeoUtil;
import com.txzmap.spliceservice.util.TileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
@Scope("prototype")
public class TileDownloadService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    TaskInfoList taskInfoList;
    @Autowired
    TbTileService tbTileService;
    @Autowired
    MyConfig myConfig;
    @Autowired
    MapSourceService mapSourceService;
    @Autowired
    RestTemplate restTemplate;


    private class TileIndex {
        int x = 0, y = 0, z = 0;

        public TileIndex(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }
    }


    /**
     * 异步执行任务 并更新redis中的状态
     *
     * @param task
     */
    @Async
    public void download(TaskInfoList.TaskInfo task) {
        //计算出要下载的瓦片总数
        MapSource mapSource = task.getMapSource();
        int z = task.getZoom();
        List<TileIndex> tilesToDownload = new ArrayList<>();
        int total = task.getTotal();
        int process = 0;
        int xMax = task.getMaxX();
        int xMin = task.getMinX();
        int yMax = task.getMaxY();
        int yMin = task.getMinY();
        String mapId = getMapIdFromUrl(mapSource.getUrl());

        //添加任务
        logger.error("任务开始，共需要处理" + total + "瓦片");

        task.setStatus(TaskInfoList.TaskStatus.DOWNLOADING);
        //先循环检测有多少存在的 如果不存在 则加入下载列表中循环下载
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                if (tbTileService.exist(mapId, x, y, z)) {
                    task.setProcess(++process);
                    taskInfoList.update(task);
                } else
                    tilesToDownload.add(new TileIndex(x, y, z));
            }
        }
        logger.debug("数据库循环查询完毕，一共有" + tilesToDownload.size() + "块需要下载 ");
        //循环下载
        int loopCounter = 0;
        while (tilesToDownload.size() > 0) {
            logger.error("开始第{}次循环", loopCounter);
            if (++loopCounter > 5) {
                //循环次数大于5次了，说明确实没有办法下载下来了 那么结束任务
                logger.error("循环次数大于5次了,结束任务");
                task.setStatus(TaskInfoList.TaskStatus.ERROR);
                taskInfoList.update(task);
                return;
            }
            Iterator<TileIndex> it = tilesToDownload.iterator();
            while (it.hasNext()) {
                TileIndex index = it.next();
                //如果下载成功则 将该条从下载列表中删除
                if (saveTile(mapSource, index.getX(), index.getY(), index.getZ())) {
                    it.remove();
                    task.setProcess(++process);
                    taskInfoList.update(task);
                }
            }
        }

        //开始拼接
        task.setStatus(TaskInfoList.TaskStatus.SPLICING);
        taskInfoList.update(task);
        String fileName = doSplice(task);
        //拼接完成
        task.setStatus(TaskInfoList.TaskStatus.FINISHED);
        task.setEndTime(System.currentTimeMillis());
        task.setUrl(fileName);
        taskInfoList.update(task);
    }


    /**
     * 拼接
     * 从数据库中循环取出对应的图片数据然后 进行拼接
     */
    private String doSplice(TaskInfoList.TaskInfo task) {
        byte[] tmp;
        Integer resolution = task.getMapSource().getTileResolution();
        int xMax = task.getMaxX();
        int xMin = task.getMinX();
        int yMax = task.getMaxY();
        int yMin = task.getMinY();
        int xSize = xMax - xMin + 1;
        int ySize = yMax - yMin + 1;
        BufferedImage[][] images = new BufferedImage[xSize][ySize];
        String mapId = getMapIdFromUrl(task.getMapSource().getUrl());
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                tmp = (byte[]) tbTileService.getTile(mapId, x, y, task.getZoom());
                try {
                    //将数据读到缓存当中
                    images[x - xMin][y - yMin] = ImageIO.read(new ByteArrayInputStream(tmp));
                } catch (IOException e) {
                    logger.error("获取" + x + "/" + y + "/" + task.getZoom() + "发生错误" + e.toString());
                }
            }
        }
        //新建一个大的图片
        BufferedImage imageNew = new BufferedImage(xSize * resolution, ySize * resolution, BufferedImage.TYPE_INT_RGB);
        int[] tmpRGBArray = new int[resolution * resolution];
        //循环铺上去
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {

                images[x - xMin][y - yMin].getRGB(0, 0, resolution, resolution, tmpRGBArray, 0, resolution);
                imageNew.setRGB((x - xMin) * resolution, (y - yMin) * resolution, resolution, resolution, tmpRGBArray, 0, resolution);
            }
        }

        if (task.getGrid() == 2)
            drawCGCS2000GaussGrid(imageNew, task);
        else if (task.getGrid() == 1)
            drawGridLine(imageNew, task);
        if (!task.getVip()) {
            drawWaterMark(imageNew);
        }

        String fName = task.getUuid() + ".jpg";
        File outFile = new File(myConfig.getDownloadPath() + fName);
        try {
            ImageIO.write(imageNew, "jpg", outFile);
            logger.debug("输出完毕！" + outFile.length() + "bytes" + "\n");
            return fName;
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }

    }

    //经纬度网格的刻度标尺
    private float getLatLonGridSpan(int zoom) {
        if (zoom >= 14)
            return 0.1f;
        if (zoom > 10)
            return 0.2f;
        if (zoom > 7)
            return 0.5f;
        if (zoom > 5)
            return 1.0f;
        if (zoom > 1)
            return 3.0f;
        return 5.0f;

    }

    private void drawWaterMark(BufferedImage image) {

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        // 创建Graphics2D对象
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // 水印文字
        String watermarkText = "txzmap.com";

        // 设置字体和颜色
        Font font = new Font("Arial", Font.BOLD, 48);
        g2d.setFont(font);
        g2d.setColor(new Color(255, 0, 0, 128)); // 半透明红色

        // 计算对角线长度
        double diagonalLength = Math.sqrt(imageWidth * imageWidth + imageHeight * imageHeight);

        // 计算文字宽度
        int textWidth = g2d.getFontMetrics().stringWidth(watermarkText);

        // 计算文字之间的间距（根据文字宽度和对角线长度均匀分布）
        int numWatermarks = (int) (diagonalLength / textWidth);
        double spacing = diagonalLength / numWatermarks;

        // 计算对角线的角度
        double angle = Math.atan2(imageHeight, imageWidth);

        // 在对角线上均匀分布水印文字
        for (int i = 0; i < numWatermarks; i++) {
            // 计算当前文字的位置（沿对角线）
            double distance = i * spacing;
            int x = (int) (distance * Math.cos(angle));
            int y = (int) (distance * Math.sin(angle));

            // 保存当前Graphics2D的变换状态
            AffineTransform originalTransform = g2d.getTransform();

            // 平移并旋转Graphics2D到当前文字的位置
            g2d.translate(x, y);
            g2d.rotate(angle);

            // 绘制水印文字
            g2d.drawString(watermarkText, 0, 0);

            // 恢复Graphics2D的原始变换状态
            g2d.setTransform(originalTransform);
        }

        // 释放Graphics2D对象
        g2d.dispose();


    }

    /**
     * 刻画经纬度线
     *
     * @param image
     * @param task
     */
    private void drawGridLine(BufferedImage image, TaskInfoList.TaskInfo task) {
        int zoom = task.getZoom();
        float gridSpan = getLatLonGridSpan(zoom);
        double[] latlngLeftTop = new double[]{1d, 1d};
        // TileUtil.tile2boundingBox(task.getMinX(), task.getMinY(), zoom).getNorthWest();
        double[] latlngRightBottom = new double[]{2d, 2d};
        //TileUtil.tile2boundingBox(task.getMaxX(), task.getMaxY(), zoom).getSouthEast();

        //配置相关的样式
        Graphics2D graphics2D = image.createGraphics();
        Font font = new Font("Arial", Font.BOLD, 16);
        graphics2D.setFont(font);
        graphics2D.setStroke(new BasicStroke(1.0f));
        graphics2D.setColor(Color.decode(task.getGridColor()));
        int width = image.getWidth();
        int height = image.getHeight();


        double latSpan = Math.abs(latlngLeftTop[0] - latlngRightBottom[0]);


        if (latSpan > gridSpan) {

            double latPerPixel = height / (latSpan / gridSpan);
            //起始点
            double lat0 = Math.ceil(latlngRightBottom[0]);

            if (gridSpan < 1) {
                lat0 = Math.ceil(latlngRightBottom[0] * 10) / 10;
            }

            int y0 = (int) (height - ((lat0 - latlngRightBottom[0]) / gridSpan) * latPerPixel);

            for (int i = 0; (y0 - latPerPixel * i) >= 0; i++) {
                graphics2D.drawLine(0, (int) (y0 - latPerPixel * i), width, (int) (y0 - latPerPixel * i));
                graphics2D.drawString(GeoUtil.df_latlon.format(lat0 + i * gridSpan) + " N", 0, (int) (y0 - latPerPixel * i) - 10);
            }

        }

        //经线
        double lngSpan = Math.abs(latlngLeftTop[1] - latlngRightBottom[1]);
        if (lngSpan > gridSpan) {
            double lngPerPixel = width / (lngSpan / gridSpan);

            double lng0 = Math.ceil(latlngLeftTop[1]);

            if (gridSpan < 1) {
                lng0 = Math.ceil(latlngLeftTop[1] * 10) / 10;
            }

            int x0 = (int) (((lng0 - latlngLeftTop[1]) / gridSpan) * lngPerPixel);

            for (int i = 0; (x0 + lngPerPixel * i) <= width; i++) {
                graphics2D.drawLine((int) (x0 + lngPerPixel * i), 0, (int) (x0 + lngPerPixel * i), height);

                graphics2D.drawString(GeoUtil.df_latlon.format(lng0 + i * gridSpan) + " E", (int) (x0 + lngPerPixel * i) + 8, 0 + 20);
            }

        }
    }

    /**
     * 只在中国境内适用  x是到赤道的距离 y是到中央经线的距离
     *
     * @param image
     * @param task
     */
    private void drawCGCS2000GaussGrid(BufferedImage image, TaskInfoList.TaskInfo task) {
        int zoom = task.getZoom();
        //13层以下无法叠加
        if (zoom < 13)
            return;
        Graphics2D graphics2D = image.createGraphics();
        Font font = new Font("Arial", Font.BOLD, 16);
        graphics2D.setFont(font);
        graphics2D.setColor(Color.decode(task.getGridColor()));
        graphics2D.setStroke(new BasicStroke(task.getGridWidth()));

        //顺时针从左上角开始分别为p1 p2 p3 p4
        GeoPos p1 = TileUtil.tile2boundingBox(task.getMinX(), task.getMinY(), zoom).getNorthWest();
        GeoPos p2 = TileUtil.tile2boundingBox(task.getMaxX(), task.getMinY(), zoom).getNorthEast();
        GeoPos p3 = TileUtil.tile2boundingBox(task.getMaxX(), task.getMaxY(), zoom).getSouthEast();
        GeoPos p4 = TileUtil.tile2boundingBox(task.getMinX(), task.getMaxY(), zoom).getSouthWest();

        //4个角的高斯坐标
        double[] xy1 = GeoUtil.WGS84ToCGCS2000(p1);
        double[] xy2 = GeoUtil.WGS84ToCGCS2000(p2);
        double[] xy3 = GeoUtil.WGS84ToCGCS2000(p3);
        double[] xy4 = GeoUtil.WGS84ToCGCS2000(p4);

        List<Double> xLeftList = findDivisibleBy1000(xy1[1], xy4[1]);
        List<Double> xRightList = findDivisibleBy1000(xy2[1], xy3[1]);

        int width = image.getWidth();
        int height = image.getHeight();

        if (xRightList.size() > 0 && xLeftList.size() > 0) {
            double xSpanLeft = Math.abs(xy1[1] - xy4[1]), xSpanRight = Math.abs(xy2[1] - xy3[1]);
            double xStartLeft = Math.min(xy1[1], xy4[1]), xStartRight = Math.min(xy2[1], xy3[1]);
            double xPerPixelLeft = height / xSpanLeft, xPerPixelRight = height / xSpanRight;
            for (double xLeft : xLeftList) {
                for (double xRight : xRightList) {
                    if (xLeft == xRight) {
                        //连成一要线
                        double yLeft = (xLeft - xStartLeft) * xPerPixelLeft;
                        double yRight = (xRight - xStartRight) * xPerPixelRight;
                        graphics2D.drawLine(0, (int) yLeft, width, (int) yRight);
                    }
                }
            }

        }

        List<Double> yListTop = findDivisibleBy1000(xy2[0], xy1[0]);
        List<Double> yListBottom = findDivisibleBy1000(xy4[0], xy3[0]);

        if (yListTop.size() > 0 && yListBottom.size() > 0) {
            yListTop.forEach(yTop -> {
                yListBottom.forEach(yBottom -> {
                    if (yTop == yBottom) {
                        //连成一要线
                    }
                });
            });
        }


//        double x1 = xy1[1];
//        double x2 = xy3[1];
//        double y1 = xy1[0];
//        double y2 = xy3[0];
//
//        double xSpanLeft = Math.abs(xy1[1] - xy4[1]);
//        double xSpanRight = Math.abs(xy2[1] - xy3[1]);
//
//        double ySpanTop = Math.abs(xy2[0] - xy1[0]);
//        double ySpanBottom = Math.abs(xy3[0] - xy4[0]);
//
//        double ySpan = y2 - y1;
//
//
//        double xPerPixel = height / 1;
//        double yPerPixel = width / ySpan;
//
//        double x0 = Math.ceil(x2 / 1000) * 1000;
//        String label;
//        if (x0 < x1) {
//            for (int i = 0; (height - (x0 + i * 1000 - x2) * xPerPixel) > 0; i++) {
//                int pt = (int) (height - (x0 + i * 1000 - x2) * xPerPixel);
//                graphics2D.drawLine(0, pt, width, pt);
//                label = GeoUtil.df_x.format(x0 + i * 1000);
//                if (i == 0)
//                    graphics2D.drawString(label, 0 + 8, pt);
//                else
//                    graphics2D.drawString(label.substring(2, 4), 0 + 8, pt - 8);
//
//            }
//        }
//        double y0 = Math.ceil(y1 / 1000) * 1000;
//        if (y0 < y2) {
//            for (int i = 0; (width - (y0 + i * 1000 - y1) * yPerPixel) > 0; i++) {
//                int pt = (int) ((y0 + i * 1000 - y1) * yPerPixel);
//                graphics2D.drawLine(pt, 0, pt, height);
//                label = GeoUtil.df_y.format(y0 + i * 1000);
//                if (i == 0)
//                    graphics2D.drawString(label, pt, 0 + 20);
//                else
//                    graphics2D.drawString(label.substring(1, 3), pt + 8, 0 + 20);
//            }
//        }

    }

    /**
     * 找出 a 和 b 之间所有可以整除 1000 的数
     *
     * @param a 起始值
     * @param b 结束值
     * @return 可以整除 1000 的数的列表
     */
    public static List<Double> findDivisibleBy1000(double a, double b) {
        List<Double> result = new ArrayList<>();

        // 确保 a 是较小的数，b 是较大的数
        double start = Math.min(a, b);
        double end = Math.max(a, b);

        // 找到第一个大于等于 start 且能整除 1000 的数
        double firstDivisible = Math.ceil(start / 1000) * 1000;

        // 遍历所有能整除 1000 的数
        for (double num = firstDivisible; num <= end; num += 1000) {
            result.add(num);
        }

        return result;
    }


    /**
     * 生成具体的url链接
     *
     * @param url
     * @param x
     * @param y
     * @param z
     * @return
     */
    private String createFinalUrl(String url, Integer x, Integer y, Integer z) {
        String newString =
                url.replace("{x}", x + "").
                        replace("{y}", y + "").
                        replace("{z}", z + "");
        return newString;
    }

    private boolean saveTile(MapSource source, Integer x, Integer y, Integer z) {
        String realUrl = createFinalUrl(source.getUrl(), x, y, z);
        String path = myConfig.getDownloadPath();
        String mapId = getMapIdFromUrl(source.getUrl());
        if (!new File(path).exists())
            new File(path).mkdirs();
        ResponseEntity<byte[]> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(realUrl, byte[].class);
        } catch (ResourceAccessException e) {
            //连接超时会抛出上面这个意外 捕获进行处理
            logger.debug("获取{}/{}/{}失败,{}", new Object[]{x, y, z, e.toString()});
            return false;
        }


        //如果返回不正常 则退出
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("获取{}/{}/{}失败", new Integer[]{x, y, z});
            return false;
        }
        //获取entity中的数据
        byte[] body = responseEntity.getBody();
        if (body != null) {
            //有可能获取为空
            TbTile tile = new TbTile(-1, mapId, x, y, z, body);
            tbTileService.insert(tile);
            logger.debug("{}/{}/{}下载成功", new Integer[]{x, y, z});
            return true;
        }
        return false;
    }

    /**
     * 通过地图的链接md5加密其后取中间的16位做为唯一标识码
     * 16位的已经足够使用了
     *
     * @param url
     * @return
     */
    private String getMapIdFromUrl(String url) {
        return DigestUtils.md5DigestAsHex(url.getBytes(StandardCharsets.UTF_8)).substring(8, 24);
    }

}
