package cloud.agileframework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author 佟盟 on 2018/9/6
 */
public class VerificationCodeExpire extends AuthenticationException {
    public VerificationCodeExpire() {
        super("验证码过期");
    }
}
