package com.txzmap.spliceservice.entity;

import lombok.Data;

/**
 * @author jayhuang
 * @date 2020-07-22 下午3:36
 */
@Data
public class RespEntity {

    /**
     * 用来设置返回数据
     */
    private Object result = "";

    /**
     * 从添加边界api开始使用 用于标记是否是成功的返回
     */
    private Integer resultCode = RespCode.CODE_OK;

    /**
     * 用于对返回状态码进行补充说明的
     */
    private String info = "操作成功";

    private String reason = "";

    public RespEntity(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public RespEntity() {
    }

    public static RespEntity err(String info) {
        RespEntity entity = new RespEntity(RespCode.CODE_ERROR);
        entity.setInfo(info);
        return entity;
    }

    public static RespEntity ok(Object object) {
        RespEntity entity = new RespEntity(RespCode.CODE_OK);
        entity.setInfo("操作成功");
        entity.setResult(object);
        return entity;
    }


}


