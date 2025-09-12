package top.zyp.boot.config.controller;

import org.springframework.web.bind.annotation.*;
import top.zyp.boot.config.common.ApiResponse;
import top.zyp.boot.config.model.enums.DrinkType;

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
@RequestMapping("/drink")
public class DrinkController {

    /**
     * 选择饮料
     */
    @GetMapping("/{type}")
    public ApiResponse<Map<String, Object>> chooseDrink(@PathVariable String type) {
        try {
            DrinkType drink = DrinkType.valueOf(type.toUpperCase());
            Map<String, Object> result = new HashMap<>();
            result.put("name", drink.getLabel());
            result.put("price", drink.getPrice());
            return ApiResponse.success("选择成功", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("饮料类型无效");
        }
    }

    /**
     * 获取饮料菜单
     */
    @GetMapping("/menu")
    public ApiResponse<List<Map<String, Object>>> getMenu() {
        List<Map<String, Object>> menu = new ArrayList<>();
        for (DrinkType drink : DrinkType.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", drink.getLabel());
            item.put("price", drink.getPrice());
            menu.add(item);
        }
        return ApiResponse.success("菜单获取成功", menu);
    }

    /**
     * 下单
     */
    @GetMapping("/order")
    public ApiResponse<Map<String, Object>> orderDrink(@RequestParam String type,
                                                       @RequestParam int num) {
        try {
            DrinkType drink = DrinkType.valueOf(type.toUpperCase());
            double total = drink.getPrice() * num;
            Map<String, Object> result = new HashMap<>();
            result.put("name", drink.getLabel());
            result.put("num", num);
            result.put("unitPrice", drink.getPrice());
            result.put("totalPrice", total);
            return ApiResponse.success("下单成功", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("饮料类型无效");
        }
    }
}
