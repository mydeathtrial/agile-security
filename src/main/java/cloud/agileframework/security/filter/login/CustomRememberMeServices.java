package cloud.agileframework.security.filter.login;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.util.date.DateUtil;
import cloud.agileframework.security.filter.token.LoginCacheInfo;
import cloud.agileframework.security.filter.token.TokenInfo;
import cloud.agileframework.security.properties.ErrorSignProperties;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.provider.LoginErrorProvider;
import cloud.agileframework.security.util.TokenUtil;
import cloud.agileframework.spring.util.IdUtil;
import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
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
    @Autowired
    private ObjectProvider<LoginErrorProvider> providers;
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
        TokenInfo tokenInfo = LoginCacheInfo.createTokenInfo(token,
                new Date(),
                DateUtil.add(new Date(), securityProperties.getTokenTimeout()));
        LoginCacheInfo loginCacheInfo = LoginCacheInfo.createLoginCacheInfo(username,
                successfulAuthentication,
                sessionToken,
                tokenInfo);

        //放入缓存
        cache.put(LoginCacheInfo.LOGIN_USER_PREFIX + username, loginCacheInfo);

        //存储登录信息
        securityUserDetailsService.loadLoginInfo(userDetails, ServletUtil.getRequestIP(request), Long.toString(sessionToken));

        //令牌传递给前端
        TokenUtil.notice(request, response, token);

        ((UsernamePasswordAuthenticationToken) successfulAuthentication).setDetails(tokenInfo);
    }


    /**
     * 登录失败计数
     *
     * @param request 请求
     */
    private void failureCount(HttpServletRequest request) {
        ErrorSignProperties errorSignProperties = securityProperties.getErrorSign();
        if (!errorSignProperties.isEnable()) {
            return;
        }

        AgileCache errorSignCache = errorSignProperties.getCache();

        //获取锁定标识
        ErrorSignInfo errorSignInfo = (ErrorSignInfo) request.getAttribute(ErrorSignInfo.REQUEST_ATTR);

        //计数过期时间
        Duration countTimeout = errorSignProperties.getCountTimeout();

        //已失败次数
        Integer errorCount = errorSignCache.get(errorSignInfo.getLockObject(), Integer.class);

        //锁定过期时间
        Duration lockTime = errorSignProperties.getLockTime();
        int maxErrorCount = errorSignProperties.getMaxErrorCount();
        errorCount = errorCount == null ? 1 : ++errorCount;
        if (errorCount < maxErrorCount) {
            errorSignCache.put(errorSignInfo.getLockObject(), errorCount, countTimeout);
        } else if (errorCount > maxErrorCount && errorSignProperties.isAutoDelay()) {
            errorSignCache.put(errorSignInfo.getLockObject(), errorCount, lockTime);
        } else if (errorCount == maxErrorCount) {
            boolean alwaysLock = lockTime.toMillis() <= 0;
            if (alwaysLock) {
                errorSignCache.put(errorSignInfo.getLockObject(), errorCount);
            } else {
                errorSignCache.put(errorSignInfo.getLockObject(), errorCount, lockTime);
                errorSignInfo.setLockTime(new Date());
                errorSignInfo.setTimeOut(new Date(errorSignInfo.getLockTime().getTime() + lockTime.toMillis()));
            }
            providers.orderedStream().forEach(provider -> provider.lock(errorSignInfo));
        }


        request.removeAttribute(ErrorSignInfo.REQUEST_ATTR);
    }

}
