package com.txzmap.spliceservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.time.LocalDateTime;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务 每天凌晨1点执行一次：0 0 1 * * ?
public class StaticScheduleTask {
    final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    MyConfig myConfig;

    //3.添加定时任务 每天的凌晨执行
    @Scheduled(cron = "0 0 1 * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
        String path = myConfig.getDownloadPath();
        File pathFile = new File(path);
        File[] files = pathFile.listFiles();
        for (File f : files) {
            f.delete();
        }
        logger.error(LocalDateTime.now() + "当日巡查结束，共删除文件" + files.length + "个");
    }
}