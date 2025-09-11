package top.zyp.boot.config.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/11
 * @Version: 1.0
 */

@Data
@Component
public class Team {
    @Value("${team.leader}")
    @NotBlank(message = "负责人不能为空")
    @Length(min = 2, max = 10, message = "长度在2-10之间")
    private String leader;

    @Value("${team.phone}")
    @Pattern(regexp = "^1[0-9]{11}$", message = "手机号码格式不正确")        //中括号[]在表示范围 {}表示位数
    private String phone;

    @Value("${team.createDate}")
    @Past(message = "创建时间必须早于当前时间")
    private LocalDate createDate;

    @Value("${team.age}")
    @Max(5)
    @Min(1)
    private Integer age;
}
