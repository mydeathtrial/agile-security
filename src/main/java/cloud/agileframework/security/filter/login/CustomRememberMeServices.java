package cloud.agileframework.security.filter.login;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.util.date.DateUtil;
import cloud.agileframework.security.config.SecurityAutoConfiguration;
import cloud.agileframework.security.filter.token.LoginCacheInfo;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.util.TokenUtil;
import cloud.agileframework.spring.util.ServletUtil;
import cloud.agileframework.spring.util.spring.IdUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.RememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author 佟盟
 * 日期 2020/8/00026 19:05
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CustomRememberMeServices implements RememberMeServices, InitializingBean {
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private CustomerUserDetailsService securityUserDetailsService;
    private AgileCache cache;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.cache = CacheUtil.getCache(securityProperties.getTokenHeader());

    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        failureCount(request);
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        UserDetails userDetails = (UserDetails) (successfulAuthentication.getPrincipal());
        String username = userDetails.getUsername();
        long sessionToken = IdUtil.generatorId();

        //创建token令牌
        String token = TokenUtil.generateToken(username, sessionToken, DateUtil.add(new Date(), Duration.of(365, ChronoUnit.DAYS)));

        //创建登录信息
        LoginCacheInfo loginCacheInfo = LoginCacheInfo.createLoginCacheInfo(username,
                successfulAuthentication,
                sessionToken,
                token,
                new Date(),
                DateUtil.add(new Date(), securityProperties.getTokenTimeout()));

        //放入缓存
        cache.put(username, loginCacheInfo);

        //存储登录信息
        securityUserDetailsService.loadLoginInfo(userDetails, ServletUtil.getRequestIP(request), Long.toString(sessionToken));

        //令牌传递给前端
        TokenUtil.notice(request, response, token);

        ((UsernamePasswordAuthenticationToken) successfulAuthentication).setDetails(token);
        request.setAttribute(SecurityAutoConfiguration.ACCESS_SUCCESS, successfulAuthentication);
    }


    /**
     * 登录失败计数
     *
     * @param request 请求
     */
    private void failureCount(HttpServletRequest request) {
        if (!securityProperties.getErrorSign().isEnable()) {
            return;
        }

        AgileCache errorSignCache = securityProperties.getErrorSign().getCache();

        //获取锁定标识
        ErrorSignInfo errorSignInfo = (ErrorSignInfo) request.getAttribute(ErrorSignInfo.REQUEST_ATTR);

        //计数过期时间
        Duration countTimeout = securityProperties.getErrorSign().getErrorSignCountTimeout();

        //已失败次数
        Integer errorCount = errorSignCache.get(errorSignInfo.getLockObject(), Integer.class);
        if (errorCount == null) {
            errorSignCache.put(errorSignInfo.getLockObject(), 1, countTimeout);
        } else {
            errorSignCache.put(errorSignInfo.getLockObject(), ++errorCount, countTimeout);
        }

        request.removeAttribute(ErrorSignInfo.REQUEST_ATTR);
    }

}
