package top.zyp.filter.interceptor.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */

@Component
@Slf4j
public class MyFilter implements Filter {
    private final HttpServletResponse httpServletResponse;

    public MyFilter(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void init(FilterConfig filterConfig) throws SecurityException {
        log.info("MyFilter 初始化");
    }



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("MyFilter 在请求接口之前执行的逻辑");
        filterChain.doFilter(servletRequest, servletResponse);
        log.info("MyFilter 在请求接口之后执行的逻辑");
    }
    @Override
    public void destroy() {
        log.info("MyFilter 销毁");
    }
}
