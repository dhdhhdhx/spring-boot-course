package top.zyp.filter.interceptor.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.zyp.filter.interceptor.interceptor.MyInterceptor;
import top.zyp.filter.interceptor.interceptor.YourInterceptor;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */

@Configuration
@AllArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {
    private final MyInterceptor myInterceptor;
    private final YourInterceptor yourInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor)
                .addPathPatterns("/api/test");
        registry.addInterceptor(yourInterceptor)
                .addPathPatterns("/api/test");
    }
}
