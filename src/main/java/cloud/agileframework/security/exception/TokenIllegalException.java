package cloud.agileframework.security.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * @author 佟盟 on 2018/7/4
 */
public class TokenIllegalException extends AccountStatusException {
    public TokenIllegalException(String msg) {
        super(msg);
    }

    public TokenIllegalException(String msg, Throwable t) {
        super(msg, t);
    }
}
