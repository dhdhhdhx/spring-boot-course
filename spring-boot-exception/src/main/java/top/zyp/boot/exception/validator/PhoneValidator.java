package top.zyp.boot.exception.validator;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/19
 * @Version: 1.0
 */


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import top.zyp.boot.exception.annotation.Phone;

import java.util.regex.Pattern;

/**
 * 手机号校验器
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    // 手机号正则表达式
    private static final String REGEX_PHONE = "^1[3456789]\\d{9}$";

    @Override
    public void initialize(Phone phone) {
        // 无需初始化操作
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 忽略空值，由 @NotBlank 注解校验
        if (value == null || value.isBlank()) {
            return true;
        }
        return Pattern.matches(REGEX_PHONE, value);
    }
}
