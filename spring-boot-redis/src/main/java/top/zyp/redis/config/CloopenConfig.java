package top.zyp.redis.config;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 短信配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "zyp.sms.ccp")
public class CloopenConfig {
    private String serverIp;
    private String port;
    private String accountSId;
    private String accountToken;
    private String appId;
    private String templateId;
}
