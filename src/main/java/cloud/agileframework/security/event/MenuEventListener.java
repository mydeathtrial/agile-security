package cloud.agileframework.security.event;

import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.security.menu.AbstractMenuDetail;
import cloud.agileframework.security.menu.MenuManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2020-12-29 11:38
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class MenuEventListener implements ApplicationListener<MenuEvent> {
    @Autowired
    private MenuManager manager;

    @Override
    public void onApplicationEvent(MenuEvent event) {
        final UserDetails userDetails = event.getUserDetails();
        if (userDetails == null) {
            return;
        }
        List<AbstractMenuDetail> menu = findMenuByUser(userDetails);
        if (CollectionUtils.isEmpty(menu)) {
            return;
        }
        CacheUtil.getCache(MenuEvent.MENU_CACHE_KEY).put(userDetails.getUsername(), menu);
    }

    private List<AbstractMenuDetail> findMenuByUser(UserDetails user) {
        if (null == user) {
            return new ArrayList<>();
        }
        return manager.byUser(user.getAuthorities());
    }
}
