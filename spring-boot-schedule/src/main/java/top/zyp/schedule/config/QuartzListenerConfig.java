package top.zyp.schedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import top.zyp.schedule.listener.GlobalJobListener;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/11
 * @Version: 1.0
 */


@Configuration
public class QuartzListenerConfig {
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // 注册全局监听器
        factory.setGlobalJobListeners(new GlobalJobListener());
        return factory;
    }
}
