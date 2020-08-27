package cloud.agileframework.security.properties;

import cloud.agileframework.security.filter.login.CustomerUserDetails;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 佟盟
 * 日期 2020/4/15 10:36
 * 描述 模拟环境，该环境下获取当前用户信息时，使用内置账户
 * @version 1.0
 * @since 1.0
 */
@ConfigurationProperties(prefix = "agile.simulation")
@Setter
@Getter
public class SimulationProperties {
    /**
     * 开关
     */
    private boolean enable = false;
    /**
     * 模拟用户
     */
    private String user;
    /**
     * 模拟用户对象的类型
     */
    private Class<? extends CustomerUserDetails> userClass;
}
