package com.txzmap.spliceservice.controller;

import com.txzmap.spliceservice.entity.*;
import com.txzmap.spliceservice.service.ActiveCodeService;
import com.txzmap.spliceservice.service.MapSourceService;
import com.txzmap.spliceservice.service.TileDownloadService;
import com.txzmap.spliceservice.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/user")
@RestController()
public class UserController extends BaseController {
    @Autowired
    MapSourceService mapSourceService;

    @PostMapping("/login")
    public RespEntity login(@RequestBody User user, HttpSession session) {
        User loginUser = userService.loginByUserNameAndPassword(user.getUserName(), user.getPassword());
        if (loginUser == null) {
            return RespEntity.err("用户名或者密码错误");
        } else {
            //生成token
            String token = JwtUtils.CreateToken(loginUser.getId());
            loginUser.setPassword("");
            loginUser.setToken(token);
            //返回token 登陆成功
            session.setAttribute("user", loginUser);
            return RespEntity.ok(loginUser);
        }
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

    @PostMapping("/changePassword")
    public RespEntity alert(HttpServletRequest request,
                            @RequestBody Map<String, Object> jsonData) {
        Integer id = getUserIdFromJWT(request);
        String oldPassword = String.valueOf(jsonData.get("oldPassword"));
        String newPassword = String.valueOf(jsonData.get("newPassword"));
        resp = new RespEntity();
        if (id != null && StringUtils.hasText(oldPassword) && StringUtils.hasText(newPassword)) {
            boolean res = userService.changePasswordById(id, oldPassword, newPassword);
            if (res) {
                resp.setResultCode(RespCode.CODE_OK);
                resp.setInfo("修改成功，请重新登陆");
                return resp;
            }
        }
        resp.setResultCode(RespCode.CODE_ERROR);
        resp.setInfo("修改失败,请检查原密码是否正确");
        return resp;
    }


    @GetMapping("/source/all")
    public RespEntity getMySource(HttpServletRequest request) {
        Integer id = getUserIdFromJWT(request);
        resp = new RespEntity();
        if (id == null) {
            resp.setResultCode(RespCode.CODE_ERROR);
            resp.setInfo("无用户信息");
            return resp;
        }
        resp.setResult(mapSourceService.getMySource(id));
        return resp;
    }

    @PostMapping("/source/add")
    public RespEntity addMapSource(HttpServletRequest request, @RequestBody MapSource source) {
        Integer id = getUserIdFromJWT(request);
        resp = new RespEntity();
        if (id == null) {
            resp.setResultCode(RespCode.CODE_ERROR);
            resp.setInfo("无用户信息");
            return resp;
        }
        source.setOwner(id);
        mapSourceService.add(source);
        resp.setResult(mapSourceService.getMySource(id));
        resp.setInfo("添加成功");
        return resp;
    }

    @GetMapping("/source/del")
    public RespEntity delMapSource(HttpServletRequest request, @RequestParam Integer sourceId) {
        Integer userId = getUserIdFromJWT(request);
        resp = new RespEntity();
        if (userId == null) {
            resp.setResultCode(RespCode.CODE_ERROR);
            resp.setInfo("无用户信息");
            return resp;
        }
        mapSourceService.delete(sourceId, userId);
        resp.setResult(mapSourceService.getMySource(userId));
        resp.setInfo("删除成功");
        return resp;
    }


    @Autowired
    ActiveCodeService activeCodeService;

    @GetMapping("/active")
    public RespEntity active(HttpServletRequest request, @RequestParam String activeCode) {
        User user = getUserFromJWT(request);
        if (user == null) {
            return RespEntity.err("无用户信息");
        }
        ActiveCode ac = activeCodeService.findByCode(activeCode);
        if (ac == null) {
            return RespEntity.err("激活失败");
        }
        if (ac.getType() == ActiveCode.ACTIVE_CODE_TYPE_MONTH) {
            user.setMonthUser();
            userService.updateVIPInfo(user);
        }
        if (ac.getType() == ActiveCode.ACTIVE_CODE_TYPE_FOREVER) {
            user.setForeverUser();
            userService.updateVIPInfo(user);
        }
        //将其置为false
        activeCodeService.usingCode(activeCode);
        return RespEntity.ok("激活成功");
    }


    @GetMapping("/info")
    public RespEntity getUserInfoByJWT(HttpServletRequest request) {
        String token = request.getHeader("Authorization");// 从 http 请求头中取出 token
        Integer id = JwtUtils.getUserIdFromToken(token);
        resp = new RespEntity();
        if (id == null) {
            return RespEntity.err("无用户信息");
        }
        User u = userService.selectUserById(id);
        u.setPassword("");
        resp.setResult(u);
        return resp;
    }

    @Autowired
    TileDownloadService tileDownloadService;

    @PostMapping("/splice")
    public RespEntity splice(HttpServletRequest request, @RequestBody TaskInfoList.TaskInfo taskInfo) {
        User user = getUserFromJWT(request);
        MapSource source = mapSourceService.getSourceById(taskInfo.getMapSourceId());
        if (source == null)
            return RespEntity.err("地图源无效");
        taskInfo.setMapSource(source);
        String taskId = UUID.randomUUID().toString();
        taskInfo.setUuid(taskId);
        int totalSize = taskInfo.getTotal();
        if (totalSize > 1000)
            return RespEntity.err("瓦片数量超过系统限制");

        if (totalSize > 100) {
            if (User.USER_TYPE_NORMAL == user.getType()) {
                return RespEntity.err("普通用户只能拼接100块的地图");
            }
            if (!user.vipIsAvailable()) {
                return RespEntity.err("用户vip已经过期");
            }
        }
        taskInfo.setVip(user.vipIsAvailable());
        taskInfo.setStartTime(System.currentTimeMillis());
        tileDownloadService.download(taskInfo);
        return RespEntity.ok(taskId);
    }


    @Autowired
    TaskInfoList taskInfoList;

    @PostMapping("/ps")
    public RespEntity updateProcessStatus(@RequestBody Map<String, List<String>> list) {

        List<String> taskIdList = list.get("list");
        List<TaskInfoList.TaskInfo> infoList = new ArrayList<>();
        for (String id : taskIdList) {
            infoList.add(taskInfoList.search(id));
        }
        return RespEntity.ok(infoList);
    }

    @Value("${myconfig.downloadPath:jayhuang}")

    String downloadPath;

    @GetMapping("/down")
    public ResponseEntity<Resource> download(@RequestParam() String name) {
        File f = new File(downloadPath + name);
        if (!f.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(f);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + f.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(f.length())
                .body(resource);

    }

}
