package com.txzmap.spliceservice.entity;

/**
 * 返回的状态值
 */
public class MyResult {
    Integer code = 1;
    /**
     * 操作结果描述
     */
    String info = "";
    /**
     * 返回的数据
     */
    Object data = null;

    public MyResult(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public MyResult(Integer code, String info, Object data) {
        this.code = code;
        this.info = info;
        this.data = data;
    }

    public MyResult(Integer code, Object data) {
        this.code = code;
        this.data = data;
        if (code == 1)
            setInfo("操作成功！");
        if (code == 0)
            setInfo("操作错误！");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
