package cloud.agileframework.security.filter.login;

import cloud.agileframework.security.provider.LoginValidateProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 佟盟 on 2017/1/13
 */
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private CustomerUserDetailsService userDetailsService;
    private ObjectProvider<LoginValidateProvider> loginValidateProviders;
    /**
     * 虚拟账户
     */
    private static UserDetails simulation;

    public void setUserDetailsService(CustomerUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setLoginValidateProviders(ObjectProvider<LoginValidateProvider> loginValidateProviders) {
        this.loginValidateProviders = loginValidateProviders;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        //验证账号合法性
        userDetailsService.validate(userDetails);

        // 额外验证
        List<LoginValidateProvider> providers = loginValidateProviders.orderedStream().collect(Collectors.toList());
        for (LoginValidateProvider provider : providers) {
            provider.validate(authentication, userDetails);
        }
    }

    /**
     * 加载用户数据
     *
     * @param username       帐号
     * @param authentication 权限
     * @return 账户信息
     * @throws AuthenticationException 账户异常
     */
    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (username != null && simulation != null && username.equals(simulation.getUsername())) {
            return simulation;
        }
        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    public static void setSimulation(UserDetails simulation) {
        JwtAuthenticationProvider.simulation = simulation;
    }
}