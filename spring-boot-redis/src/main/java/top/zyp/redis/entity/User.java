package top.zyp.redis.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Version;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/26
 * @Version: 1.0
 */


@Data
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登陆账号 (唯一，必填)
     */
    @NotBlank(message = "账号不能为空")
    @Size(max = 50, message = "账号长度不能超过50")
    private String account;

    /**
     * 密码(存BCrypt哈希，不存明文)
     */
    @NotBlank(message = "密码不能为空")
    @Size(max = 255, message = "密码长度过长")
    private String password;

    /**
     * 昵称 (可空)
     */
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    /**
     * 手机号 (唯一，可空)
     */
    @Size(max = 20, message = "手机号长度不能超过20")
    private String phone;

    /**
     * 头像URL (可空)
     */
    @Size(max = 255, message = "头像URL长度不能超过255")
    private String avatar;

    /**
     * 逻辑删除：1-已删除，0-未删除（默认未删除）
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    /**
     * 乐观锁版本号 (整型)
     */
    @Version
    private Integer version;

    /**
     * 创建时间（插入自动填充），时间格式化，东八区
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间（插入与更新自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
