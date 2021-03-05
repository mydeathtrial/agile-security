package cloud.agileframework.security.menu;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2020-12-29 13:42
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface MenuManager {
    /**
     * 根据帐号查询可见菜单项
     *
     * @param authorities 权限信息集合
     * @return 菜单集合
     */
    List<AbstractMenuDetail> byUser(Collection<? extends GrantedAuthority> authorities);
}
