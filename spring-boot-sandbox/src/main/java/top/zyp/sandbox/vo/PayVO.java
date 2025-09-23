package top.zyp.sandbox.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/23
 * @Version: 1.0
 */

@Data
public class PayVO {
    // 商户订单号 必填
    private String outTradeNo;
    // 订单名称 必填
    private String subject;
    // 付款金额 必填
    private BigDecimal totalAmount;
}
