package top.zyo.filterinterceptor.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.zyo.filterinterceptor.common.CachedBodyHttpServletRequest;

import java.io.IOException;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/8
 * @Version: 1.0
 */


@Component
public class CachedBodyFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CachedBodyFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        logger.info("↓↓ CachedBodyFilter enter: {} {}, class={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getClass().getSimpleName());

        if ("POST".equalsIgnoreCase(request.getMethod()) &&
                request.getContentType() != null &&
                request.getContentType().contains("application/json")) {

            CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);
            chain.doFilter(wrapped, response);   // ✅ 拼写正确
        } else {
            chain.doFilter(request, response);
        }
    }
}
