package top.zyp.redis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/27
 * @Version: 1.0
 */

@Data
public class UserRegisterDTO implements Serializable {

    @NotBlank(message = "账号不能为空")
    @Size(max = 50, message = "账号长度不能超过50")
    private String account;

    @NotBlank(message = "密码不能为空")
    @Size(max = 255, message = "密码长度过长")
    private String password;

    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

}
