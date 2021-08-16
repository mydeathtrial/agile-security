package cloud.agileframework.security.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author 佟盟
 * 日期 2021-07-15 11:42
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class AccessEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public AccessEvent(Object source) {
        super(source);
    }
}
