package cloud.agileframework.security.provider;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 会话验证
 */
public interface TokenValidateProvider {
    void validate(UserDetails userDetails) throws AuthenticationException;
}
