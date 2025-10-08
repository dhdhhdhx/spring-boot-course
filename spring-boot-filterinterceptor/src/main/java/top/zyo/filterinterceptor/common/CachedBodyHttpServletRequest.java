package top.zyo.filterinterceptor.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * @Author: calm_sunset
 * @Date: 2025/10/8
 * @Version: 1.0
 */

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    private final String body;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        body = new String(request.getInputStream().readAllBytes());
    }

    public String getBody() {
        return body;
    }
}
