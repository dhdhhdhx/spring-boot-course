package top.zyp.boot.exception.service;

import org.springframework.stereotype.Service;
import top.zyp.boot.exception.enums.ErrorCode;
import top.zyp.boot.exception.exception.ServerException;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/19
 * @Version: 1.0
 */

@Service
public class TestService {
    public void method1() {
        throw new ServerException(ErrorCode.FORBIDDEN);
    }
    public void method2() {
        throw new ServerException("余额不足");
    }
}
