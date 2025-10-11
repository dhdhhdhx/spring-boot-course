package top.zyp.schedule.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/11
 * @Version: 1.0
 */


@Configuration
@Slf4j
public class ScheduledExceptionHandler {

    @Bean
    public TaskScheduler taskScheduler1() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("scheduled-task");
        // 全局异常处理器
        scheduler.setErrorHandler(t -> {
            log.error("定时任务全局异常: {}", t.getCause().getMessage());
        });
        scheduler.initialize();
        return scheduler;
    }
}
