package top.zyp.boot.config.controller;

import org.springframework.web.bind.annotation.*;
import top.zyp.boot.config.model.enums.ExpressStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/12
 * @Version: 1.0
 */


@RestController
@RequestMapping("/express")
public class ExpressController {

    /**
     * 获取快递状态
     */
    @GetMapping("/{status}")
    public String checkExpressStatus(@PathVariable ExpressStatus status) {
        return "快递状态：" + status.getLabel();
    }

    /**
     * 获取所有快递状态
     */
    @GetMapping("/statuses")
    public List<Map<String, String>> getAllStatuses() {
        List<Map<String, String>> list = new ArrayList<>();
        for (ExpressStatus status : ExpressStatus.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("value", status.name());
            item.put("label", status.getLabel());
            list.add(item);
        }
        return list;
    }

    /**
     * 更新快递状态
     */
    @PostMapping("/update")
    public String updateExpress(@RequestParam String orderNo,
                                @RequestParam ExpressStatus status) {
        return "快递单号" + orderNo + "状态已更新为：" + status.getLabel();
    }
}
