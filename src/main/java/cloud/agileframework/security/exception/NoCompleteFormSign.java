package cloud.agileframework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author 佟盟 on 2018/7/4
 */
public class NoCompleteFormSign extends AuthenticationException {
    public NoCompleteFormSign() {
        super("表单不完整");
    }
}
