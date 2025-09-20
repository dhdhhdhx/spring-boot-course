package top.zyp.boot.exception.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import top.zyp.boot.exception.annotation.Phone;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/19
 * @Version: 1.0
 */

@Data
public class User {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Max(value = 120, message = "年龄不能大于120岁")
    @Min(value = 1, message = "年龄不能小于1岁")
    private int age;

    @NotBlank(message = "手机号不能为空")
    @Phone
    private String phone;
}
