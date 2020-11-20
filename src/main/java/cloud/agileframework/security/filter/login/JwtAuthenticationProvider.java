package cloud.agileframework.security.filter.login;

import cloud.agileframework.security.util.PasswordUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author 佟盟 on 2017/1/13
 */
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private CustomerUserDetailsService userDetailsService;
    /**
     * 虚拟账户
     */
    private static UserDetails simulation;

    public void setUserDetailsService(CustomerUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        //验证账号合法性
        userDetailsService.validate(userDetails);

        //验证密码
        checkPassword(authentication, userDetails);
    }

    /**
     * 加载用户数据
     *
     * @param username       帐号
     * @param authentication 权限
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if(username!=null && simulation!=null && username.equals(simulation.getUsername())){
            return simulation;
        }
        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    /**
     * 校验密码
     */
    private void checkPassword(Authentication authentication, UserDetails user) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            String cipher = user.getPassword();
            if (!PasswordUtil.decryption(presentedPassword, cipher)) {
                throw new BadCredentialsException(String.format("密码匹配失败,[输入项：%s][目标值：%s]", presentedPassword, cipher));
            }
        }
    }

    public static void setSimulation(UserDetails simulation) {
        JwtAuthenticationProvider.simulation = simulation;
    }
}