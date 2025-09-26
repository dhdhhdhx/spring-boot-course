package top.zyp.filter.interceptor.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */

@Component
@Slf4j
public class YourInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LocalDateTime beginTime = (LocalDateTime) request.getAttribute("beginTime");
        LocalDateTime endTime = LocalDateTime.now();

        Duration duration = Duration.between(beginTime, endTime);
        long millisseconds = duration.toMillis();

        String requestURI = request.getRequestURI();
        log.info("YourInterceptor 在请求接口之后执行的逻辑,请求接口为：{}，耗时为：{}毫秒", requestURI, millisseconds);
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("YourInterceptor 在请求接口之后执行的逻辑,请求接口为：{}", requestURI);
        LocalDateTime beginTime = LocalDateTime.now();
        request.setAttribute("beginTime", beginTime);
        return true;
    }
}
