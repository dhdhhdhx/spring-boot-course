package top.zyp.filter.interceptor.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.zyp.filter.interceptor.filter.RateLimitFilter;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */

@Configuration
@AllArgsConstructor
public class FilterConfigTest {
//    private final MyFilter myFilter;
//    private final YourFilter yourFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public FilterRegistrationBean<RateLimitFilter> YourFilterRegistrationBean() {
        FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(rateLimitFilter);
        registrationBean.addUrlPatterns("/api/pay/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

//    @Bean
//    public FilterRegistrationBean<MyFilter> myFilterRegistrationBean() {
//        FilterRegistrationBean<MyFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(myFilter);
//        registrationBean.addUrlPatterns("/api/test");
////        registrationBean.setOrder(1);
//        return registrationBean;
//    }
//    @Bean
//    public FilterRegistrationBean<YourFilter> YourFilterRegistrationBean() {
//        FilterRegistrationBean<YourFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(yourFilter);
//        registrationBean.addUrlPatterns("/api/test");
////        registrationBean.setOrder(1);
//        return registrationBean;
//    }
}
