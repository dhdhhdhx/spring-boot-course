package top.zyp.redis.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/27
 * @Version: 1.0
 */

@Data
public class UserRegisterVO {

    private Long    id;
    private String  account;
    private String  nickname;
    private String  phone;
    private String  avatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}
