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
public class ExceptionService {

    public void unAuthorizedError() {
        // 未登录
        throw new ServerException(ErrorCode.UNAUTHORIZED);
    }

    public void systemError() {
        // 系统异常
        throw new ServerException("系统异常");
    }
}
