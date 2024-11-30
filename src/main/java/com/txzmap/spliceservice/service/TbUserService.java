package com.txzmap.spliceservice.service;

import com.txzmap.spliceservice.entity.TbUser;
import com.txzmap.spliceservice.mapper.TbUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TbUserService {

    @Autowired
    private TbUserMapper tbUserMapper;


    public TbUser get(String userName, String password) {
        return tbUserMapper.get(userName, password);
    }

    public void insert(TbUser user) {
        tbUserMapper.insert(user);
    }

    public void changePassword(String userName, String password) {
        tbUserMapper.changePassword(userName, password);
    }

}

