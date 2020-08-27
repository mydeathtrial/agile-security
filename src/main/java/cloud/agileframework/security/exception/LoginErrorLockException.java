package cloud.agileframework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author 佟盟
 * 日期 2019/5/15 11:13
 * 描述 登陆错误，并上锁
 * @version 1.0
 * @since 1.0
 */
public class LoginErrorLockException extends AuthenticationException {

    public LoginErrorLockException() {
        super("超过登陆失败限定次数");
    }

    public LoginErrorLockException(String msg) {
        super(msg);
    }
}
