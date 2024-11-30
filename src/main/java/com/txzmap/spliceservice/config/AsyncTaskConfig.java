package com.txzmap.spliceservice.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncTaskConfig implements AsyncConfigurer {

    /**
     * 核心线程数
     */
//    @Value("${threadPool.corePoolSize}")
    private int corePoolSize = 10;
    /**
     * 最大线程数
     */
    private int maxPoolSize = 50;
    /**
     * 线程池缓冲队列容量
     */
    private int queueCapacity = 10;
    /**
     * 空闲线程销毁前等待时长
     */
    private int awaitTerminationSeconds = 10;
    /**
     * 线程名前缀
     */
    private String threadNamePrefix = "txzmap-tile-download";

    /**
     * ThreadPoolTaskExcutor运行原理
     * 当线程池的线程数小于corePoolSize，则新建线程入池处理请求
     * 当线程池的线程数等于corePoolSize，则将任务放入Queue中，线程池中的空闲线程会从Queue中获取任务并处理
     * 当Queue中的任务数超过queueCapacity，则新建线程入池处理请求，但如果线程池线程数达到maxPoolSize，将会通过RejectedExecutionHandler做拒绝处理
     * 当线程池的线程数大于corePoolSize时，空闲线程会等待keepAliveTime长时间，如果无请求可处理就自行销毁
     */

    @Override
    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(corePoolSize);
        threadPool.setMaxPoolSize(maxPoolSize);
        threadPool.setQueueCapacity(queueCapacity);
        threadPool.setAwaitTerminationSeconds(awaitTerminationSeconds);
        threadPool.setThreadNamePrefix(threadNamePrefix);

        //关机时，是否等待任务执行完
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        //设置拒绝策略
        //CALLER_RUNS：由调用者所在的线程执行该任务
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        //初始化线程
        threadPool.initialize();
        return threadPool.getThreadPoolExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
