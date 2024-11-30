package com.txzmap.spliceservice.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
      //  registry.addViewController("/").setViewName("forward:login.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册TestInterceptor拦截器
      //  InterceptorRegistration registration = registry.addInterceptor(new MyInterceptor());
        //先将所有的请求都拦截起来
        //然后把login还有静态页面的资源全部排除
        //registration.addPathPatterns("/**"); //所有路径都被拦截
       // registration.excludePathPatterns(    //添加不拦截路径
       //         "/login.html","/login",                    //登录路径
        //        "/css/*", "/element-ui/**", "/iconfont/*", "/image/*", "/js/*", "/leaflet/*","/today"
        //);
    }
}
