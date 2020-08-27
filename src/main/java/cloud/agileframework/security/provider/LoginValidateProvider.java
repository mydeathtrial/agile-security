package cloud.agileframework.security.provider;

import org.springframework.security.core.AuthenticationException;

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
     * @param request  请求
     * @param response 响应
     * @throws AuthenticationException 登陆失败异常
     */
    void validate(HttpServletRequest request, HttpServletResponse response,String username,String password) throws AuthenticationException;
}
