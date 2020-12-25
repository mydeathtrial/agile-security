package cloud.agileframework.security.util;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.security.filter.token.LoginCacheInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 佟盟
 * 日期 2020-12-25 20:01
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class ContextUtil {

    public static List<LoginCacheInfo> currentUsers() {
        final AgileCache cache = LoginCacheInfo.getCache();
        List<String> keys = cache.keys(LoginCacheInfo.LOGIN_USER_PREFIX + "*");
        return keys.stream().map(key -> cache.get(key, LoginCacheInfo.class)).collect(Collectors.toList());
    }
}
