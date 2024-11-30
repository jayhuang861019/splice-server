package com.txzmap.spliceservice.entity;

public class MapSource {
    Integer id;
    String sourceName;
    String url;
    //分辨率
    Integer tileResolution = 256;
    //瓦片大小
    Integer tileFileSize = 20 * 1024;
    //0 系统内置 1用户自定义
    Integer type=0;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public MapSource(Integer id, String sourceName, String url, Integer tileResolution, Integer tileFileSize, Integer type) {
        this.id = id;
        this.sourceName = sourceName;
        this.url = url;
        this.tileResolution = tileResolution;
        this.tileFileSize = tileFileSize;
        this.type = type;
    }

    public MapSource(Integer id, String sourceName, String url, Integer tileResolution, Integer tileFileSize) {
        this.id = id;
        this.sourceName = sourceName;
        this.url = url;
        this.tileResolution = tileResolution;
        this.tileFileSize = tileFileSize;
    }

    public MapSource(String sourceName, String url, Integer tileResolution, Integer tileFileSize) {
        this.sourceName = sourceName;
        this.url = url;
        this.tileResolution = tileResolution;
        this.tileFileSize = tileFileSize;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getTileResolution() {
        return tileResolution;
    }

    public void setTileResolution(Integer tileResolution) {
        this.tileResolution = tileResolution;
    }

    public Integer getTileFileSize() {
        return tileFileSize;
    }

    public void setTileFileSize(Integer tileFileSize) {
        this.tileFileSize = tileFileSize;
    }
}
