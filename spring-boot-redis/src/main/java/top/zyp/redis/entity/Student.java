package top.zyp.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

    private String name;
    private Integer age;
    private Address address;

}