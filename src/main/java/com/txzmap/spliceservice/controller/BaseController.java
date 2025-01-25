package com.txzmap.spliceservice.controller;

import com.txzmap.spliceservice.entity.RespEntity;
import com.txzmap.spliceservice.entity.User;
import com.txzmap.spliceservice.service.UserService;
import com.txzmap.spliceservice.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
    @Autowired
    UserService userService;

    protected RespEntity resp;

    protected Integer getUserIdFromJWT(HttpServletRequest request) {
        String token = request.getHeader("Authorization");// 从 http 请求头中取出 token
        Integer id = JwtUtils.getUserIdFromToken(token);
        return id;
    }

    protected User getUserFromJWT(HttpServletRequest request) {
        Integer userId = getUserIdFromJWT(request);
        if (userId == null) return null;
        return userService.selectUserById(userId);
    }
}
