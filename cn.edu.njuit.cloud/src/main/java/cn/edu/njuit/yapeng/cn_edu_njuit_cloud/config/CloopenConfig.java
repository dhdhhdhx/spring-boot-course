package cn.edu.njuit.yapeng.cn_edu_njuit_cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/5
 * @Version: 1.0
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "cn.edu.njuit")
public class CloopenConfig {
    private String serverIp;
    private String port;
    private String accountSId;
    private String accountToken;
    private String appId;
    private String templateId;
}
