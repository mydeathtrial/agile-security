package cloud.agileframework.security.filter.login;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 佟盟
 * 日期 2019/3/15 12:50
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface CustomerUserDetailsService extends UserDetailsManager {
    /**
     * 用户身份验证
     *
     * @param securityUser 用户信息
     * @throws AuthenticationException 身份验证失败异常
     */
    default void validate(UserDetails securityUser) throws AuthenticationException {
    }

    /**
     * 更新登录信息
     *
     * @param userName 帐号
     * @param oldToken 旧会话令牌
     * @param newToken 新会话令牌
     */
    default void updateLoginInfo(String userName, String oldToken, String newToken) {
    }

    /**
     * 终止
     *
     * @param userName 帐号
     * @param token    令牌
     */
    default void stopLoginInfo(String userName, String token) {
    }

    /**
     * 新增登录信息
     *
     * @param securityUser 用户信息
     * @param ip           登录ip
     * @param token        令牌
     */
    default void loadLoginInfo(UserDetails securityUser, String ip, String token) {
    }

    /**
     * 提取账户信息
     *
     * @param request  请求
     * @param username 帐号
     * @return 当前账号信息
     */
    default UserDetails extract(HttpServletRequest request, String username) {
        final String springSecurityCurrentUser = "SPRING_SECURITY_CURRENT_USER";
        UserDetails user = (UserDetails) request.getAttribute(springSecurityCurrentUser);
        if (user == null) {
            user = loadUserByUsername(username);
        }

        request.setAttribute(springSecurityCurrentUser, user);
        return user;
    }
}
