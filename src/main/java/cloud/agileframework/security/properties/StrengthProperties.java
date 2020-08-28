package cloud.agileframework.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2020/8/00028 11:05
 * 描述 强度权重配置
 * @version 1.0
 * @since 1.0
 */
@ConfigurationProperties(prefix = "agile.security.password.strength-conf")
@Data
public class StrengthProperties implements Serializable {
    /**
     * 最大允许密码长度
     */
    private int maxLength;
    /**
     * 正则权重
     */
    private double weightOfRegex;
    /**
     * 关键字权重
     */
    private double weightOfKeyWord;
    /**
     * 正则配置
     */
    private List<WeightMap> weightOfRegexMap;
    /**
     * 关键字配置
     */
    private List<String> weightOfKeyWords;
}
