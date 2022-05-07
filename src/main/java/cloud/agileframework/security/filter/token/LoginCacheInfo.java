package cloud.agileframework.security.filter.token;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.date.DateUtil;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.security.exception.NoSignInException;
import cloud.agileframework.security.exception.TokenIllegalException;
import cloud.agileframework.security.filter.login.CustomerUserDetailsService;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.util.TokenUtil;
import cloud.agileframework.spring.util.BeanUtil;
import cloud.agileframework.spring.util.IdUtil;
import cloud.agileframework.spring.util.ServletUtil;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 佟盟
 * 日期 2019/3/20 19:03
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class LoginCacheInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private Authentication authentication;
    private Map<Long, TokenInfo> sessionTokens = new HashMap<>();
    private static SecurityProperties securityProperties = BeanUtil.getBean(SecurityProperties.class);
    public static final String LOGIN_USER_PREFIX = "LOGIN_USER_";
    private static AgileCache cache = CacheUtil.getCache(Objects.requireNonNull(BeanUtil.getBean(SecurityProperties.class)).getTokenHeader());

    public static AgileCache getCache() {
        return cache;
    }

    private static CustomerUserDetailsService customerUserDetailsService = BeanUtil.getBean(CustomerUserDetailsService.class);


    /**
     * 创建登录信息
     *
     * @param username       账号
     * @param authentication 用户权限信息
     * @param sessionToken   本次会话令牌
     * @return
     */
    public static LoginCacheInfo createLoginCacheInfo(String username, Authentication authentication, Long sessionToken, TokenInfo tokenInfo) {
        LoginCacheInfo loginCacheInfo = cache.get(LOGIN_USER_PREFIX + username, LoginCacheInfo.class);
        Map<Long, TokenInfo> sessionTokens;

        if (loginCacheInfo == null) {
            loginCacheInfo = new LoginCacheInfo();
            loginCacheInfo.setUsername(username);
            loginCacheInfo.setAuthentication(authentication);
            sessionTokens = new HashMap<>(Constant.NumberAbout.ONE);
        } else {
            loginCacheInfo.setUsername(username);
            loginCacheInfo.setAuthentication(authentication);
            sessionTokens = loginCacheInfo.getSessionTokens();
            parsingTimeOut(sessionTokens);
        }
        sessionTokens.put(sessionToken, tokenInfo);
        loginCacheInfo.setSessionTokens(sessionTokens);
        return loginCacheInfo;
    }

    /**
     * 创建token信息
     *
     * @param token 令牌
     * @param start 开始时间
     * @param end   结束时间
     * @return token信息
     */
    public static TokenInfo createTokenInfo(String token, Date start, Date end) {
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        tokenInfo.setStart(start);
        tokenInfo.setEnd(end);
        tokenInfo.setIp(ServletUtil.getRequestIP(ServletUtil.getCurrentRequest()));

        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getCurrentRequest().getHeader("User-Agent"));
        tokenInfo.setBrowser(userAgent.getBrowser());
        Version version = userAgent.getBrowserVersion();
        tokenInfo.setBrowserVersion(version == null ? null : version.getVersion());
        tokenInfo.setOs(userAgent.getOperatingSystem());

        return tokenInfo;
    }

    /**
     * 处理掉过期时间
     */
    public void parsingTimeOut() {
        parsingTimeOut(sessionTokens);
    }

    /**
     * 处理过期数据
     *
     * @param sessionTokens 会话令牌集合
     */
    private static void parsingTimeOut(Map<Long, TokenInfo> sessionTokens) {
        if (sessionTokens == null) {
            return;
        }
        sessionTokens.values().removeIf(tokenInfo -> !tokenInfo.getEnd().after(DateUtil.getCurrentDate()));
    }

    /**
     * 根据token令牌获取用户缓存信息
     *
     * @param token 令牌
     * @return 用户缓存信息
     */
    public static CurrentLoginInfo getCurrentLoginInfo(String token) {
        if (StringUtil.isBlank(token)) {
            throw new NoSignInException("账号尚未登录");
        }

        Claims claims = TokenUtil.getClaimsFromToken(token);
        if (claims == null) {
            throw new TokenIllegalException("身份令牌验证失败");
        } else {
            return refreshTimeOut(claims);
        }
    }

    /**
     * 刷新身份令牌
     *
     * @param currentLoginInfo 当前登陆用户信息
     * @return 新令牌
     */
    public static String refreshToken(CurrentLoginInfo currentLoginInfo) {
        LoginCacheInfo loginCacheInfo = currentLoginInfo.getLoginCacheInfo();
        Long oldSessionToken = currentLoginInfo.getSessionToken();

        //创建新会话令牌
        long newSessionToken = IdUtil.generatorId();

        //删除旧的缓存会话令牌
        loginCacheInfo.getSessionTokens().remove(oldSessionToken);

        //生成新的会话令牌缓存
        String token = TokenUtil.generateToken(currentLoginInfo.getLoginCacheInfo().getUsername(), newSessionToken, DateUtil.add(new Date(), Duration.of(365, ChronoUnit.DAYS)));

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        tokenInfo.setStart(new Date());
        tokenInfo.setEnd(DateUtil.add(new Date(), securityProperties.getTokenTimeout()));
        loginCacheInfo.getSessionTokens().put(newSessionToken, tokenInfo);

        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtil.getCurrentRequest().getHeader("User-Agent"));
        tokenInfo.setBrowser(userAgent.getBrowser());
        tokenInfo.setBrowserVersion(userAgent.getBrowserVersion().getVersion());
        tokenInfo.setOs(userAgent.getOperatingSystem());

        //同步缓存
        cache.put(LOGIN_USER_PREFIX + loginCacheInfo.getUsername(), loginCacheInfo);

        //更新数据库登录信息
        CustomerUserDetailsService securityUserDetailsService = BeanUtil.getBean(CustomerUserDetailsService.class);
        assert securityUserDetailsService != null;
        securityUserDetailsService.updateLoginInfo(loginCacheInfo.getUsername(), Long.toString(oldSessionToken), Long.toString(newSessionToken));

        return token;
    }

    /**
     * 验证当前redis缓存数据是否合法
     *
     * @param loginCacheInfo 登陆信息
     */
    public static void validateCacheDate(LoginCacheInfo loginCacheInfo) {
        LoginCacheInfo info = Optional.ofNullable(loginCacheInfo).orElseThrow(() -> new UsernameNotFoundException("Not Found Account"));
        customerUserDetailsService.validate((UserDetails) info.getAuthentication().getPrincipal());
    }

    /**
     * 刷新token过期时间
     *
     * @param claims 令牌信息
     * @return 当前登陆信息
     */
    private static CurrentLoginInfo refreshTimeOut(Claims claims) {
        Long sessionToken = claims.get(TokenUtil.AUTHENTICATION_SESSION_TOKEN, Long.class);
        String username = claims.get(TokenUtil.AUTHENTICATION_USER_NAME, String.class);

        LoginCacheInfo loginCacheInfo = cache.get(LOGIN_USER_PREFIX + username, LoginCacheInfo.class);

        if (loginCacheInfo == null) {
            throw new TokenIllegalException("无效身份令牌");
        }

        // 处理过期
        loginCacheInfo.parsingTimeOut();

        TokenInfo sessionInfo = loginCacheInfo.getSessionTokens().get(sessionToken);
        if (sessionInfo == null) {
            throw new TokenIllegalException("身份令牌已过期");
        }
        if (!claims.getExpiration().after(DateUtil.getCurrentDate())) {
            throw new TokenIllegalException("身份令牌已过期");
        }

        sessionInfo.setEnd(DateUtil.add(new Date(), securityProperties.getTokenTimeout()));
        cache.put(LOGIN_USER_PREFIX + username, loginCacheInfo);

        return new CurrentLoginInfo(sessionToken, loginCacheInfo);
    }

    /**
     * 退出操作，根据token删除指定会话令牌
     *
     * @param token 令牌
     */
    public static void remove(String token) {
        remove(getCurrentLoginInfo(token));
    }

    /**
     * 退出操作，根据currentLoginInfo删除指定会话令牌
     *
     * @param currentLoginInfo 当前登录信息
     */
    public static void remove(CurrentLoginInfo currentLoginInfo) {
        currentLoginInfo.getLoginCacheInfo().getSessionTokens().remove(currentLoginInfo.getSessionToken());
        if (currentLoginInfo.getLoginCacheInfo().getSessionTokens().size() > 0) {
            cache.put(LOGIN_USER_PREFIX + currentLoginInfo.getLoginCacheInfo().getUsername(), currentLoginInfo.getLoginCacheInfo());
        } else {
            cache.evict(LOGIN_USER_PREFIX + currentLoginInfo.getLoginCacheInfo().getUsername());
        }
    }
}
