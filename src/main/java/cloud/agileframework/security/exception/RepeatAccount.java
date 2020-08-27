package cloud.agileframework.security.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * @author 佟盟 on 2018/7/6
 */
public class RepeatAccount extends AccountStatusException {
    public RepeatAccount(String msg) {
        super(msg);
    }

    public RepeatAccount(String msg, Throwable t) {
        super(msg, t);
    }
}
