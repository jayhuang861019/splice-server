package com.txzmap.spliceservice.controller;

import com.txzmap.spliceservice.config.MyConfig;
import com.txzmap.spliceservice.entity.MapSource;
import com.txzmap.spliceservice.entity.MyResult;
import com.txzmap.spliceservice.entity.TaskInfoList;
import com.txzmap.spliceservice.entity.TbUser;
import com.txzmap.spliceservice.service.MapSourceService;
import com.txzmap.spliceservice.service.TbTileService;
import com.txzmap.spliceservice.service.TbUserService;
import com.txzmap.spliceservice.service.TileDownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@EnableScheduling
public class UserController {
    final Logger logger = LoggerFactory.getLogger(getClass().getName());

    Integer todayViewCount = 0;

    @Autowired
    MapSourceService mapSourceService;

    @Autowired
    TbUserService tbUserService;

    @Autowired
    private TileDownloadService tileDownloadService;

    @Autowired
    TbTileService tbTileService;

    @Autowired
    MyConfig myConfig;

    @Autowired
    TaskInfoList taskInfoList;

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @GetMapping("/getSource")
    public List<MapSource> getMapSourceList() {
        return mapSourceService.getAll();
    }


    @PostMapping("/addSource")
    public MyResult add(@RequestParam(required = true, value = "sourceName") String name,
                        @RequestParam(required = true, value = "url") String url,
                        Integer resolution,
                        Integer tileFileSize) {
        MapSource source = new MapSource(name, url, resolution, tileFileSize);
        mapSourceService.add(source);
        return new MyResult(1, "添加成功！");
    }


    @GetMapping("/delete")
    public MyResult deleteMapSource(@RequestParam(required = true) Integer id) {

        mapSourceService.delete(id);
        return new MyResult(1, "添加成功！");
    }

    @PostMapping("/splice")
    public MyResult splice(@RequestParam(required = true) Integer mapId,
                           @RequestParam(required = true, value = "x1") Integer leftTopTileX,
                           @RequestParam(required = true, value = "y1") Integer leftTopTileY,
                           @RequestParam(required = true, value = "x2") Integer rightBottomTileX,
                           @RequestParam(required = true, value = "y2") Integer rightBottomTileY,
                           @RequestParam Integer z,
                           String name,
                           @RequestParam(defaultValue = "0") Integer grid,
                           @RequestParam(defaultValue = "#000000") String gridColor
    ) {
        //生成taskId
        String taskId = UUID.randomUUID().toString();
        MapSource mapSource = mapSourceService.get(mapId);
        if (mapSource == null) {
            return new MyResult(0, "地图源不存在！");
        }
        Integer[] leftTop = new Integer[]{leftTopTileX, leftTopTileY};
        Integer[] rightBottom = new Integer[]{rightBottomTileX, rightBottomTileY};

        TaskInfoList.TaskInfo taskInfo = new TaskInfoList.TaskInfo(taskId, name, mapSource, leftTop, rightBottom, z, grid, gridColor);

        logger.error(taskInfo.toString());

        if (taskInfo.getTotal() > 1000)
            return new MyResult(0, "瓦片数量大于1000,测试版无法下载！");
        taskInfo.setStartTime(System.currentTimeMillis());
        tileDownloadService.download(taskInfo);
        return new MyResult(1, "任务添加成功！", taskId);
    }

    @GetMapping("/ps")
    public TaskInfoList.TaskInfo updateProcessStatus(@RequestParam(value = "id", required = true) String taskId) {
        return taskInfoList.search(taskId);
    }

    @GetMapping("/down")
    public void download(@RequestParam() String name, HttpServletResponse response) throws IOException {
        // 读到流中
        File f = new File(myConfig.getDownloadPath() + name);
        if (!f.exists()) {
            logger.error(name + "文件不存在！");
            return;
        }
        InputStream inputStream = new FileInputStream(myConfig.getDownloadPath() + name);// 文件的存放路径
        response.reset();
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(name, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int len;
        //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
        while ((len = inputStream.read(b)) > 0) {
            outputStream.write(b, 0, len);
        }
        inputStream.close();
    }


    @GetMapping("/login")
    public MyResult login(@RequestParam String userName, @RequestParam String password, HttpServletRequest request) {
        TbUser user = tbUserService.get(userName, password);


        if (user == null)
            return new MyResult(0, "用户名或者密码不存在！");
        else {
            //写入用户信息
            request.getSession().setAttribute("user", user);
            todayViewCount++;
            return new MyResult(1, "登陆成功！");
        }
    }

    @GetMapping("/today")
    public Integer getTodayViewCount() {
        return todayViewCount;
    }

    /**
     * 随机生成账号
     *
     * @return
     */
    @GetMapping("/new")
    public TbUser createNewAccount() {
        String userName = UUID.randomUUID().toString().split("-")[0];
        String password = "123456";
        Long createTime = System.currentTimeMillis();
        Long expirationTime = createTime + 24 * 60 * 60 * 1000;
        TbUser user = new TbUser(userName, password, expirationTime, createTime);
        tbUserService.insert(user);
        return user;
    }

    /**
     * 随机生成账号
     *
     * @return
     */
    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        session.removeAttribute("user");
        try {
            //通知前端跳转
            response.sendRedirect(request.getContextPath() + "/login.html");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @PostMapping("/change")
    public MyResult changePwd(HttpServletRequest request, String nowPassword, String newPassword) {
        HttpSession session = request.getSession();
        TbUser user = (TbUser) session.getAttribute("user");
        if (user.getUserName().equals("test")) {
            return new MyResult(0, "请不要修改测试账号的密码！");
        }
        if (user == null) {
            return new MyResult(0, "请登陆！");
        }

        if (user.getPassword().equals(nowPassword)) {
            tbUserService.changePassword(user.getUserName(), newPassword);
            return new MyResult(1, "密码修改成功！");
        }
        return new MyResult(0, "原密码不正确！");

    }


    @PostMapping("/info")
    public Map<String, String> getUserInfo(HttpServletRequest request, String nowPassword, String newPassword) {
        Map<String, String> resultMap = new HashMap<>();

        HttpSession session = request.getSession();
        TbUser user = (TbUser) session.getAttribute("user");

        resultMap.put("userName", user.getUserName());
        resultMap.put("createTime", sdf.format(user.getCreateTime()));
        resultMap.put("expiration", sdf.format(user.getExpiration()));
        return resultMap;
    }


    //3.添加定时任务 每天的凌晨执行
    @Scheduled(cron = "0 0 1 * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void resetViewCounter() {
        this.todayViewCount = 0;
    }


}


