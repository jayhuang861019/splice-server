package com.txzmap.spliceservice.controller;

import com.txzmap.spliceservice.entity.RespCode;
import com.txzmap.spliceservice.entity.RespEntity;
import com.txzmap.spliceservice.entity.User;
import com.txzmap.spliceservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController()
public class UserController extends BaseController {
    @Autowired
    UserService userService;


    @PostMapping("/login")
    public RespEntity login(@RequestBody User user) {
        resp = new RespEntity();
        User loginUser = userService.loginByUserNameAndPassword(user.getUserName(), user.getPassword());
        if (loginUser == null) {
            resp.setResultCode(RespCode.CODE_ERROR);
            resp.setInfo("用户名或者密码错误");
        } else {
            resp.setInfo("登陆成功");
            resp.setResult(loginUser);
        }
        return resp;
    }


    @PostMapping("/register")
    public RespEntity register(@RequestBody User user) {
        resp = new RespEntity();
        if (StringUtils.hasText(user.getUserName()) && StringUtils.hasText(user.getPassword())) {
            boolean exist = userService.existsByUserName(user.getUserName());
            if (!exist) {
                user.setCreateTime(System.currentTimeMillis());
                user.setType(User.USER_TYPE_NORMAL);
                user.setExpiration(0l);
                userService.insertUser(user);
                resp.setInfo("注册成功");
                return resp;
            }
            resp.setResultCode(RespCode.CODE_ERROR);
            resp.setInfo("用户名已存在！");
            return resp;
        }
        resp.setResultCode(RespCode.CODE_ERROR);
        resp.setInfo("注册失败，请检查用户名和密码");
        return resp;
    }


    @RequestMapping("/logout")
    public RespEntity logout() {

        resp.setInfo("退出");
        return resp;
    }

    @PostMapping("/alert")
    public RespEntity alert(@RequestBody User user, @RequestParam("newPassword") String newPassword) {

        resp.setInfo("修改用户");
        return resp;
    }


}
