package cloud.agileframework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author 佟盟 on 2018/9/6
 */
public class VerificationCodeNon extends AuthenticationException {

    public VerificationCodeNon() {
        super("未检测到验证码");
    }
}
