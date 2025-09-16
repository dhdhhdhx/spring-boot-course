package top.zyp.boot.config.config;

import cn.hutool.ai.core.AIService;
import cn.hutool.ai.core.BaseConfig;
import cn.hutool.ai.model.deepseek.DeepSeekServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.zyp.boot.config.model.DeepSeekProperties;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/13
 * @Version: 1.0
 */


@Configuration
@RequiredArgsConstructor
public class AIConfig {

    private final DeepSeekProperties p;

    @Bean
    public AIService aiService() {

        // 5.8.38+ 唯一入口：BaseConfig + 具体实现类
        BaseConfig config = new BaseConfig();
        config.setApiKey(p.getApiKey());
        config.setApiUrl(p.getApiUrl());
        config.setModel(p.getModel());

        // 想换厂商就换实现类：OpenAIServiceImpl / DoubaoServiceImpl ...
        return new DeepSeekServiceImpl(config);
    }
}