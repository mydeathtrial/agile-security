package cloud.agileframework.security.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 佟盟
 * 日期 2020/8/00028 11:07
 * 描述 正则权重映射
 * @version 1.0
 * @since 1.0
 */
@Data
public class WeightMap implements Serializable {
    private String regex;
    private double weight;
}
