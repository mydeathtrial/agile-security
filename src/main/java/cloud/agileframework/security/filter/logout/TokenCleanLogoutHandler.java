package cloud.agileframework.security.filter.logout;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.security.filter.login.CustomerUserDetailsService;
import cloud.agileframework.security.filter.token.CurrentLoginInfo;
import cloud.agileframework.security.filter.token.LoginCacheInfo;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.provider.LogoutProcessorProvider;
import cloud.agileframework.spring.util.ParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author 佟盟 on 2018/7/6
 */
public class TokenCleanLogoutHandler implements LogoutHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String LOGOUT_USERNAME = "SPRING_SECURITY_LOGOUT_USERNAME";
    public static final String LOGOUT_TOKEN = "SPRING_SECURITY_LOGOUT_TOKEN";
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private CustomerUserDetailsService securityUserDetailsService;
    @Autowired
    private ObjectProvider<LogoutProcessorProvider> observers;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //获取令牌
        String token = ParamUtil.getInfo(request, securityProperties.getTokenHeader());
        if (token == null) {
            return;
        }

        //获取当前登录信息
        CurrentLoginInfo currentLoginInfo = LoginCacheInfo.getCurrentLoginInfo(token);
        String username = currentLoginInfo.getLoginCacheInfo().getUsername();
        String sessionToken = Long.toString(currentLoginInfo.getSessionToken());

        request.setAttribute(TokenCleanLogoutHandler.LOGOUT_USERNAME, username);
        request.setAttribute(TokenCleanLogoutHandler.LOGOUT_TOKEN, token);

        //更新数据库
        securityUserDetailsService.stopLoginInfo(username, sessionToken);

        //更新缓存
        LoginCacheInfo.remove(currentLoginInfo);

        //清空header信息
        cleanHeader(response);

        //清空cookie信息
        cleanCookie(request, response);

        //执行后钩子
        after(username, token);

        logger.info(String.format("账号退出[username:%s][token：%s]", username, sessionToken));
    }

    /**
     * 清空头部信息
     *
     * @param httpServletResponse 响应
     */
    private void cleanHeader(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader(securityProperties.getTokenHeader(), Constant.RegularAbout.BLANK);
    }

    /**
     * 清空cookies
     */
    private void cleanCookie(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return;
        }
        Optional<Cookie> cookieOptional = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equalsIgnoreCase(securityProperties.getTokenHeader()))
                .findFirst();
        if (!cookieOptional.isPresent()) {
            return;
        }
        Cookie cookie = cookieOptional.get();
        cookie.setValue(null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath(Constant.RegularAbout.SLASH);
        httpServletResponse.addCookie(cookie);
    }

    private void after(String username, String token) {
        observers.stream().forEach(node -> {
            try {
                node.after(username, token);
            } catch (Exception ignored) {
            }
        });
    }

}
