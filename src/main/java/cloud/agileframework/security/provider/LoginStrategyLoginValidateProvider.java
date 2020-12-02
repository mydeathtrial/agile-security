package cloud.agileframework.security.provider;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.security.exception.RepeatAccount;
import cloud.agileframework.security.filter.login.CustomerUserDetails;
import cloud.agileframework.security.properties.LoginStrategy;
import cloud.agileframework.security.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

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
    public void validate(Authentication authentication, UserDetails user) throws AuthenticationException {
        if (!(user instanceof CustomerUserDetails)) {
            return;
        }
        final LoginStrategy loginStrategy = ((CustomerUserDetails) user).getLoginStrategy();
        if (loginStrategy != null) {
            switch (loginStrategy) {
                case SINGLETON_REPLACE:
                    AgileCache cache = CacheUtil.getCache(securityProperties.getTokenHeader());
                    cache.evict(user.getUsername());
                    break;
                case MORE:
                    break;
                default:
                    throw new RepeatAccount("重复账户登录");
            }
        }
    }
}
