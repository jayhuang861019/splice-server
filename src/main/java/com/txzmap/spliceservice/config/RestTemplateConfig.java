package com.txzmap.spliceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

/**
 * RestTemplate配置类
 * 1设置模拟浏览器的请求
 * 2设置错误处理 不管什么 错误 都直接 返回给
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new restTemplateInterceptor()));
        restTemplate.setErrorHandler(new RestTemplateErrorHandler());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5 * 1000);//单位为ms
        factory.setConnectTimeout(5 * 1000);//单位为ms
        return factory;
    }

    /**
     * 这里用来模拟浏览器的请求
     */

    private class restTemplateInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            HttpHeaders headers = request.getHeaders();
            // 这里的参数要看自己需要什么
            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36";
            headers.add(HttpHeaders.USER_AGENT, userAgent);
            return execution.execute(request, body);
        }
    }


    private class RestTemplateErrorHandler implements ResponseErrorHandler {

        //    @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            // 返回false表示不管response的status是多少都返回没有错
            // 这里可以自己定义那些status code你认为是可以抛Error
            return false;
        }

        //    @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            // 这里面可以实现你自己遇到了Error进行合理的处理

        }
    }

}