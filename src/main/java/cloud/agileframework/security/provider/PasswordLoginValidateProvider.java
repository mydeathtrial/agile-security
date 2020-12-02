package cloud.agileframework.security.provider;

import cloud.agileframework.security.util.PasswordUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author 佟盟
 * 日期 2020-11-30 17:41
 * 描述 密码比对
 * @version 1.0
 * @since 1.0
 */
public class PasswordLoginValidateProvider implements LoginValidateProvider {
    @Override
    public void validate(Authentication authentication, UserDetails user) throws AuthenticationException {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            String cipher = user.getPassword();
            if (!PasswordUtil.decryption(presentedPassword, cipher)) {
                throw new BadCredentialsException(String.format("密码匹配失败,[输入项：%s][目标值：%s]", presentedPassword, cipher));
            }
        }
    }
}
