package cloud.agileframework.security.properties;

import cloud.agileframework.common.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author 佟盟
 * 日期 2020/8/00028 10:55
 * 描述 密码
 * @version 1.0
 * @since 1.0
 */
@ConfigurationProperties(prefix = "agile.security.password")
@Data
public class PasswordProperties implements Serializable {
    /**
     * 密码最低强度
     */
    private float strength = Constant.NumberAbout.FIVE;

    /**
     * 密码有效期
     */
    private Duration duration = Duration.ofDays(Constant.NumberAbout.THIRTY_ONE);

    /**
     * 密钥
     */
    private String key = "";

    /**
     * 偏移量
     */
    private String offset = "";

    /**
     * 算法模式
     */
    private String algorithmModel = "AES/CBC/PKCS5Padding";


    /**
     * 强度解析器配置
     */
    private StrengthProperties strengthConf = new StrengthProperties();
}
