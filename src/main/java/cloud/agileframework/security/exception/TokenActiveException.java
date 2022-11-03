package cloud.agileframework.security.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * 令牌活跃过期
 * @author 佟盟 on 2018/7/4
 */
public class TokenActiveException extends AccountStatusException {
    public TokenActiveException(String msg) {
        super(msg);
    }

    public TokenActiveException(String msg, Throwable t) {
        super(msg, t);
    }
}
