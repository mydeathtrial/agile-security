package cloud.agileframework.security.provider;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟
 * 日期 2020/8/00026 14:33
 * 描述 登陆验证过程调用
 * @version 1.0
 * @since 1.0
 */
public interface LoginValidateProvider {
    /**
     * 扩展登陆验证
     *
     * @param password 前端传递的密码
     * @param username 前端传递的帐号
     * @param request  请求
     * @param response 响应
     * @throws AuthenticationException 登陆失败异常
     */
    default void validate(HttpServletRequest request, HttpServletResponse response, String username, String password) throws AuthenticationException {
    }

    /**
     * 扩展登陆验证
     *
     * @param user 账户信息
     * @throws AuthenticationException 登陆失败异常
     */
    default void validate(Authentication authentication, UserDetails user) throws AuthenticationException {
    }
}
