package top.zyp.boot.config.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/13
 * @Version: 1.0
 */


@ConfigurationProperties(prefix = "ai.deepseek")
@Data   // ← 生成 getter/setter
@Component
public class DeepSeekProperties {
    private String apiKey;
    private String apiUrl;   // getter 自动生成
    private String model;
}
