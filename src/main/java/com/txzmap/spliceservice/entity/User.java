package com.txzmap.spliceservice.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Data
public class User {
    public static final int USER_TYPE_NORMAL = 0;
    public static final int USER_VIP_MONTH = 1;
    public static final int USER_VIP_FOREVER = 2;

    private Integer id;
    private String userName;
    private String password;
    private Integer type;
    private Long expiration;
    private Long lastIn;
    private Long createTime;
    private String token;

    /**
     * 是否可用
     *
     * @return
     */
    public boolean vipIsAvailable() {
        if (type == USER_TYPE_NORMAL)
            return false;
        return System.currentTimeMillis() - this.expiration < 0;
    }

    public void setMonthUser() {
        type = USER_VIP_MONTH;
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 当前时间向后推一个月
        LocalDateTime oneMonthLater = now.plus(1, ChronoUnit.MONTHS);
        // 将LocalDateTime转换为时间戳
        expiration = oneMonthLater.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    }

    public void setForeverUser() {

        type = USER_VIP_FOREVER;
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 当前时间向后推99年
        LocalDateTime ninetyNineYearsLater = now.plus(99, ChronoUnit.YEARS);
        // 将LocalDateTime转换为时间戳
        expiration = ninetyNineYearsLater.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
