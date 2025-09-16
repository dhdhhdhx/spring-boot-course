package top.zyp.boot.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/16
 * @Version: 1.0
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaVo {
    private Boolean success;   // 是否生成成功
    private String  message;   // 提示语
    private String  image;     // Base64 图片（无头）
    private String  key;       // 前端隐藏字段（可选）
}
