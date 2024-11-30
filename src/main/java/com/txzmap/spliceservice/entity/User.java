package com.txzmap.spliceservice.entity;

import lombok.Data;

@Data
public class User {
    public static final int USER_TYPE_NORMAL = 0;
    public static final int USER_VIP_MONTH = 1;
    public static final int USER_VIP_FOREVER = 2;


    private Long id;
    private String userName;
    private String password;
    private Integer type;
    private Long expiration;
    private Long lastIn;
    private Long createTime;

    /**
     * 是否可用
     *
     * @return
     */
    public boolean isAvaliable() {

        return System.currentTimeMillis() - this.expiration < 0;
    }


}
