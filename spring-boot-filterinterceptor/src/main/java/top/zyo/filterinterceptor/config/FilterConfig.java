package top.zyo.filterinterceptor.config;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/7
 * @Version: 1.0
 */


import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import top.zyo.filterinterceptor.filter.CachedBodyFilter;
import top.zyo.filterinterceptor.filter.CorsFilter;
import top.zyo.filterinterceptor.filter.LogFilter;
import top.zyo.filterinterceptor.filter.RateLimitFilter;

import java.util.ArrayList;
import java.util.List;


@Configuration
@AllArgsConstructor
public class FilterConfig {
    private final LogFilter logFilter;
    private final RateLimitFilter rateLimitFilter;
    private final CorsFilter corsFilter;
    @Bean
    public FilterRegistrationBean<LogFilter> logFilterFilterRegistrationBean() {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<>();
        // 设置过滤器实例
        registration.setFilter(logFilter);
        // 设置拦截路径（/*匹配所有请求）
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        registration.setUrlPatterns(urlPatterns);
        // 设置执行顺序（值越小越先执行）
        registration.setOrder(2);
        // 排除静态资源（可选）
        registration.addInitParameter("exclusions", "*.js,*.css,*.png");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterFilterRegistrationBean() {
        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(rateLimitFilter);
        // 仅对/api/pay/路径限流
        registration.addUrlPatterns("/api/pay/*");
        // 晚于日志过滤器
        registration.setOrder(3);
        return registration;
    }
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(corsFilter);
        registration.setUrlPatterns(List.of("/*"));
        // 最高优先级，确保跨域处理最先执行
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<CachedBodyFilter> cachedBodyFilterRegistration() {
        FilterRegistrationBean<CachedBodyFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CachedBodyFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 最先执行
        return bean;
    }

}