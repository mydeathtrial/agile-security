package cloud.agileframework.security.provider;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟
 * 日期 2020/8/00026 14:15
 * 描述 认证结果处理，用于订制响应认证成功/失败/退出等响应信息
 * @version 1.0
 * @since 1.0
 */
public interface SecurityResultProvider {
    /**
     * 认证失败处理
     *
     * @param request  请求
     * @param response 响应
     * @param e        异常
     * @return 响应视图，默认会将异常抛出，供统一异常处理器处理响应
     * @throws Throwable 异常
     */
    default Object accessException(HttpServletRequest request, HttpServletResponse response, Throwable e) throws Throwable {
        throw e;
    }

    /**
     * 登陆成功处理
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 认证成功后的权限数据，其中包含账号信息、令牌信息、权限集合信息
     * @return 响应视图
     */
    default Object loginSuccess(HttpServletRequest request, HttpServletResponse response, UsernamePasswordAuthenticationToken authentication) {
        return authentication;
    }

    /**
     * 退出成功
     *
     * @param request  请求
     * @param response 响应
     * @param username 帐号
     * @param token    令牌
     * @return 响应视图
     */
    default Object logoutSuccess(HttpServletRequest request, HttpServletResponse response, String username, String token) {
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
        modelAndView.addObject("username", username);
        modelAndView.addObject("token", token);
        return modelAndView;
    }
}
