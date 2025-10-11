package top.zyp.websocket;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/11
 * @Version: 1.0
 */
@EnableScheduling // 启用定时任务
@SpringBootApplication
public class WebSocketApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(WebSocketApplication.class, args);
    }
}
