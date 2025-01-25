package com.txzmap.spliceservice.entity;

import lombok.Data;

@Data
public class MapSource {
    Integer id;
    String name;
    String url;
    //分辨率
    Integer tileResolution = 256;
    //瓦片大小
    Integer tileFileSize = 20 * 1024;
    //0 系统内置 1用户自定义
    Integer type = 0;

    //用于定义地图源的所属
    Integer owner = -1;

    //版权说明
    String attribution;
}
