package top.zyp.filter.interceptor.filter;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: mqxu
 * @Date: 2025/9/26
 * @Description：限流过滤器
 */
@Slf4j
@Component
public class RateLimitFilter implements Filter {

    private static final int LIMIT_COUNT = 3;
    private static final int LIMIT_SECONDS = 60;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RateLimitFilter 初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        // 设置 MIME 类型
        httpResponse.setContentType("application/json;charset=UTF-8");

        String userId = httpRequest.getHeader("userId");
        if (userId == null || userId.isEmpty()) {
            httpResponse.getWriter().write("{\"code\":400,\"msg\":\"userId 不能为空\"}");
            return;
        }

        String limitKey = "rate_limit:" + userId + ":" + httpRequest.getRequestURI();
        String countStr = stringRedisTemplate.opsForValue().get(limitKey);
        int count = countStr == null ? 0 : Integer.parseInt(countStr);

        if (count >= LIMIT_COUNT) {
            httpResponse.setStatus(429);
            httpResponse.getWriter().write("{\"code\":429,\"msg\":\"请求过于频繁，请 1 分钟后再试\"}");
            return;
        }

        if (count == 0) {
            stringRedisTemplate.opsForValue().set(limitKey, "1", LIMIT_SECONDS, TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().increment(limitKey);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        log.info("RateLimitFilter 销毁");
    }
}
