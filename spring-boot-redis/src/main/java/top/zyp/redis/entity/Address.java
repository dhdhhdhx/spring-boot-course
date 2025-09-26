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
public class Address {


    private String city;
    private String province;

}
