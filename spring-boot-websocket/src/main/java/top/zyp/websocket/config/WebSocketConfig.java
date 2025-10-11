package top.zyp.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import top.zyp.websocket.handler.MonitorWebSocketHandler;
import top.zyp.websocket.handler.SimpleTimeWebSocketHandler;
import top.zyp.websocket.handler.TestWebSocketHandler;
import top.zyp.websocket.handler.WeatherWebSocketHandler;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/11
 * @Version: 1.0
 */


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public WeatherWebSocketHandler weatherWebSocketHandler() {
        return new WeatherWebSocketHandler();
    }
    @Bean
    public MonitorWebSocketHandler monitorWebSocketHandler() {
        return new MonitorWebSocketHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 测试的 WebSocket 端点
        registry.addHandler(new TestWebSocketHandler(), "/ws/test")
                .setAllowedOrigins("*");
        // 时间推送的 WebSocket 端点
        registry.addHandler(new SimpleTimeWebSocketHandler(), "/ws/time").setAllowedOrigins("*");
        // 天气预报的 WebSocket 端点
        registry.addHandler(weatherWebSocketHandler(), "/ws/weather")
                .setAllowedOrigins("*");
        // 服务器监控的 WebSocket 端点
        registry.addHandler(monitorWebSocketHandler(), "/ws/monitor")
                .setAllowedOrigins("*");
    }
}
