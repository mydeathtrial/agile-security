package cloud.agileframework.security.exception;

import javax.servlet.ServletException;

/**
 * @author 佟盟
 * 日期 2020/5/8 16:30
 * 描述 权限异常
 * @version 1.0
 * @since 1.0
 */
public class AuthenticationException extends ServletException {
    public AuthenticationException(Throwable rootCause) {
        super(rootCause);
    }
}
