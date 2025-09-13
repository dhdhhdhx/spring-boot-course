package top.zyp.boot.config.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultStatus {
    SUCCESS("发送成功"),
    FAIL("发送失败");
    private final String info;
}
