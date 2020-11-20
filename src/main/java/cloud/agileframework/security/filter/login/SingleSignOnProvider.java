package cloud.agileframework.security.filter.login;

import org.springframework.security.core.Authentication;

/**
 * @author 佟盟
 * 日期 2020-11-16 13:38
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface SingleSignOnProvider {
    /**
     * 登录
     * @param username 帐号
     * @return
     */
    Authentication sign(String username);
}
