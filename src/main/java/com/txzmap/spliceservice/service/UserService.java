package com.txzmap.spliceservice.service;


import com.txzmap.spliceservice.entity.User;
import com.txzmap.spliceservice.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }


    public void updateUser(User user) {
        userMapper.updateUser(user);
    }


    public User selectUserById(Integer id) {
        return userMapper.selectUserById(id);
    }

    public boolean existsByUserName(String userName) {
        int count = userMapper.countByUserName(userName);
        return count > 0;
    }

    public User loginByUserNameAndPassword(String userName, String password) {
        User loginUser = userMapper.selectUserByUserNameAndPassword(userName, password);
        if (loginUser == null)
            return null;
        //需要更新一下最后的登陆时间
        loginUser.setLastIn(System.currentTimeMillis());
        updateUser(loginUser);
        return loginUser;
    }

    public boolean changePasswordById(Integer id, String oldPassword, String newPassword) {
        return userMapper.updatePasswordById(id, oldPassword, newPassword) > 0;
    }

    public int updateVIPInfo(User user) {
        return userMapper.updateVIPInfo(user);
    }
}

