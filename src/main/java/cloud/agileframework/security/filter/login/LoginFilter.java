package cloud.agileframework.security.filter.login;

import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.provider.LoginValidateProvider;
import cloud.agileframework.security.provider.PasswordProvider;
import cloud.agileframework.spring.util.RequestWrapper;
import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 佟盟 on 2017/1/13
 */
public class LoginFilter extends AbstractAuthenticationProcessingFilter implements InitializingBean, SingleSignOnProvider, ApplicationContextAware {

    @Autowired
    private JwtAuthenticationProvider loginStrategyProvider;

    @Autowired
    private CustomerUserDetailsService userDetailsService;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private PasswordProvider passwordProvider;

    @Autowired
    private ObjectProvider<LoginValidateProvider> loginValidateProviders;

    @Autowired
    private RememberMeServices rememberMeServices;

    private ApplicationContext applicationContext;

    public LoginFilter(String loginUrl) {
        super(new AntPathRequestMatcher(loginUrl));
    }

    @Override
    public void afterPropertiesSet() {
        setAllowSessionCreation(false);

        this.setAuthenticationSuccessHandler(new ForwardAuthenticationSuccessHandler(securityProperties.getSuccessForwardUrl()));
        this.setAuthenticationFailureHandler(new ForwardAuthenticationFailureHandler(securityProperties.getFailForwardUrl()));

        ProviderManager providerManager = new ProviderManager(Collections.singletonList(loginStrategyProvider));
        providerManager.setEraseCredentialsAfterAuthentication(false);
        this.setAuthenticationManager(providerManager);

        loginStrategyProvider.setUserDetailsService(userDetailsService);
        loginStrategyProvider.setLoginValidateProviders(loginValidateProviders);
        setRememberMeServices(rememberMeServices);
        setApplicationEventPublisher(applicationContext);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 判断模拟账户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return authentication;
        }

        request = RequestWrapper.extract(request);
        // 获取用户名密码
        String sourceUsername = ((RequestWrapper) request).getInParam(securityProperties.getLoginUsername(), String.class);
        String sourcePassword = ((RequestWrapper) request).getInParam(securityProperties.getLoginPassword(), String.class);

        // 密码解密
        sourcePassword = passwordProvider.decrypt(sourcePassword);

        // 额外验证
        List<LoginValidateProvider> providers = loginValidateProviders.orderedStream().collect(Collectors.toList());
        for (LoginValidateProvider provider : providers) {
            provider.validate(request, response, sourceUsername, sourcePassword);
        }

        // 生成认证信息
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(sourceUsername, sourcePassword);
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }


    private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    @Override
    public Authentication sign(String username) {
        // 生成认证信息
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, null);
        Authentication auth = this.getAuthenticationManager().authenticate(authRequest);
        getRememberMeServices().loginSuccess(ServletUtil.getCurrentRequest(),
                ServletUtil.getCurrentResponse(),
                auth);
        return auth;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
