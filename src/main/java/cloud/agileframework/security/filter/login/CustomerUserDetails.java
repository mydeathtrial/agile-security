package cloud.agileframework.security.filter.login;

import cloud.agileframework.security.properties.LoginStrategy;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author 佟盟
 * 日期 2019/3/15 12:15
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface CustomerUserDetails extends UserDetails {
    /**
     * 主键
     *
     * @return 主键
     */
    Long id();

    /**
     * 登录策略
     *
     * @return 用户登录策略
     */
    LoginStrategy getLoginStrategy();
}
