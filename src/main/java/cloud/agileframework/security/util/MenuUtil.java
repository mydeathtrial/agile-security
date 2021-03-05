package cloud.agileframework.security.util;

import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.security.event.MenuEvent;
import cloud.agileframework.security.menu.AbstractMenuDetail;
import cloud.agileframework.spring.util.SecurityUtil;
import com.google.common.collect.Lists;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;

import javax.security.auth.login.AccountException;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2020-12-29 14:57
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class MenuUtil {
    private static ApplicationEventPublisher eventPublisher;

    public static void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        MenuUtil.eventPublisher = eventPublisher;
    }

    public static List<AbstractMenuDetail> menus(Object currentBean) throws AccountException {
        UserDetails currentUser = SecurityUtil.currentUser();
        if (null == currentUser) {
            throw new AccountException("尚未登陆");
        }
        List<AbstractMenuDetail> menus = CacheUtil.getCache(MenuEvent.MENU_CACHE_KEY).get(currentUser.getUsername(), new TypeReference<List<AbstractMenuDetail>>() {
        });
        if (menus == null || menus.isEmpty()) {
            refresh(currentBean, currentUser);
        }
        menus = CacheUtil.getCache(MenuEvent.MENU_CACHE_KEY).get(currentUser.getUsername(), new TypeReference<List<AbstractMenuDetail>>() {
        });
        if (menus == null) {
            menus = Lists.newArrayList();
        }
        return menus;
    }

    public static void refresh(Object currentBean, UserDetails currentUser) {
        eventPublisher.publishEvent(new MenuEvent(currentBean, currentUser));
    }


}
