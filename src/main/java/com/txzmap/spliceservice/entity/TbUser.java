package com.txzmap.spliceservice.entity;


public class TbUser implements Cloneable{

    private Integer id;
    private String userName;
    private String password;
    private Long expiration;
    private Long createTime;

    public TbUser(String userName, String password, Long expiration, Long createTime) {
        this.userName = userName;
        this.password = password;
        this.expiration = expiration;
        this.createTime = createTime;
    }

    public TbUser(Integer id, String userName, String password, Long expiration, Long createTime) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.expiration = expiration;
        this.createTime = createTime;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 是否可用
     *
     * @return
     */
    public boolean isAvaliable() {

        return System.currentTimeMillis() - this.expiration < 0;
    }


    @Override
    public TbUser clone() throws CloneNotSupportedException {
        return (TbUser) super.clone();
    }

}
