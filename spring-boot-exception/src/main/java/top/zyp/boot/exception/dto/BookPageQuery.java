package top.zyp.boot.exception.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/22
 * @Version: 1.0
 */

@Data
public class BookPageQuery {
    @Min(1)
    private Integer current = 1;

    @Min(1)
    private Integer size = 10;
}
