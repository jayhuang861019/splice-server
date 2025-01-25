package com.txzmap.spliceservice.entity;

import lombok.Data;

@Data
public class ActiveCode {
    /**
     * 激活类型月卡
     */
    public static final int ACTIVE_CODE_TYPE_MONTH = 1;
    /**
     * 激活类型永久
     */
    public static final int ACTIVE_CODE_TYPE_FOREVER = 2;
    private Integer id;
    private String code;
    private Integer type;
    private Integer available;
}
