package top.zyp.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/11
 * @Version: 1.0
 */


// 开启异步支持
@EnableAsync
@Configuration
public class AsyncConfig {

    // 配置异步线程池
    @Bean("asyncTaskExecutor")
    public Executor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 核心线程数
        executor.setMaxPoolSize(10); // 最大线程数
        executor.setQueueCapacity(20); // 队列容量
        executor.setThreadNamePrefix("async-task-"); // 线程名称前缀
        executor.initialize();
        return executor;
    }
}
