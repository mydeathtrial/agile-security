package cloud.agileframework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author 佟盟 on 2018/7/4
 */
public class VerificationCodeException extends AuthenticationException {
    public VerificationCodeException() {
        super("无效验证码");
    }

    public VerificationCodeException(String msg) {
        super(msg);
    }
}
