package com.txzmap.spliceservice.entity;


public class TbTile {

    private Integer id;
    /**
     * 由url生成的唯一标识码
     */
    private String mapId;
    private Integer X;
    private Integer Y;
    private Integer Z;
    //blob二进帛的内容默认mybatis读取出来就是object 这个要去serivice层去做转换
    /**
     * 瓦片的二进制数据s
     */
    private Object tile = null;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }


    public Integer getX() {
        return X;
    }

    public void setX(Integer X) {
        this.X = X;
    }


    public Integer getY() {
        return Y;
    }

    public void setY(Integer Y) {
        this.Y = Y;
    }


    public Integer getZ() {
        return Z;
    }

    public void setZ(Integer Z) {
        this.Z = Z;
    }


    public Object getTile() {
        return tile;
    }

    public void setTile(Object tile) {
        this.tile = tile;
    }

    public TbTile(Integer id, String mapId, Integer x, Integer y, Integer z, Object tile) {
        this.id = id;
        this.mapId = mapId;
        X = x;
        Y = y;
        Z = z;
        this.tile = tile;
    }

}
