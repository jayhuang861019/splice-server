package com.txzmap.spliceservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.txzmap.spliceservice.mapper")
@EnableAsync
//开启多线程支持
//https://blog.csdn.net/qq_41358151/article/details/112260509 关于多线程的使用
public class SpliceserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpliceserviceApplication.class, args);
    }

}
