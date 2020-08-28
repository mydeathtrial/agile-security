package cloud.agileframework.security.provider;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.security.exception.RepeatAccount;
import cloud.agileframework.security.filter.login.CustomerUserDetails;
import cloud.agileframework.security.filter.token.LoginCacheInfo;
import cloud.agileframework.security.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟
 * 日期 2020/8/00027 18:09
 * 描述 判断登录策略
 * @version 1.0
 * @since 1.0
 */
public class LoginStrategyLoginValidateProvider implements LoginValidateProvider {

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void validate(HttpServletRequest request, HttpServletResponse response, String username, String password) throws AuthenticationException {
        loginStrategyHandler(username);
    }

    /**
     * 判断登录策略
     *
     * @param username 帐号
     */
    private void loginStrategyHandler(String username) {
        AgileCache cache = CacheUtil.getCache(securityProperties.getTokenHeader());

        LoginCacheInfo loginCacheInfo = cache.get(username, LoginCacheInfo.class);

        if (loginCacheInfo == null) {
            return;
        }

        CustomerUserDetails userDetails = (CustomerUserDetails) loginCacheInfo.getAuthentication().getPrincipal();

        if (userDetails.getLoginStrategy() != null && !loginCacheInfo.getSessionTokens().isEmpty()) {
            switch (userDetails.getLoginStrategy()) {
                case SINGLETON_REPLACE:
                    cache.evict(username);
                    break;
                case MORE:
                    break;
                default:
                    throw new RepeatAccount("重复账户登录");
            }
        }
    }
}
