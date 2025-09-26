package top.zyp.redis.exception;

import lombok.Getter;
import lombok.Setter;
import top.zyp.redis.enums.ErrorCode;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */


@Getter
@Setter
public class ServerException extends RuntimeException{
    private  int code;
    private  String msg;

    public ServerException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg() ;
    }

    public ServerException(String msg) {
        super(msg);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.msg = msg;
    }

}
