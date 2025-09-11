package top.mqxu.week1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/6
 * @Version: 1.0
 */


@Data
@Configuration
@ConfigurationProperties(prefix = "cn.edu.zyp")
public class CloopenConfig {

    private String serverIp;
    private String port;
    private String accountSid;
    private String accountToken;
    private String appId;
    private String templateId;
}
