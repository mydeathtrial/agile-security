package cloud.agileframework.security.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author 佟盟
 * 日期 2020-12-29 11:37
 * 描述 菜单事件
 * @version 1.0
 * @since 1.0
 */
public class MenuEvent extends ApplicationEvent {
    public static final String MENU_CACHE_KEY = "user-menu";
    @Getter
    private final UserDetails userDetails;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MenuEvent(Object source, UserDetails userDetails) {
        super(source);
        this.userDetails = userDetails;
    }
}
