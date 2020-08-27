package cloud.agileframework.security.config;

import cloud.agileframework.cache.support.AgileCacheManagerInterface;
import cloud.agileframework.security.controller.ForwardController;
import cloud.agileframework.security.filter.login.JwtAuthenticationProvider;
import cloud.agileframework.security.filter.login.LoginFilter;
import cloud.agileframework.security.filter.logout.TokenCleanLogoutHandler;
import cloud.agileframework.security.filter.token.TokenFilter;
import cloud.agileframework.security.properties.SecurityProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.logout.ForwardLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 佟盟 on 2017/9/26
 */
@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ImportAutoConfiguration(SecurityAboutConfiguration.class)
@EnableConfigurationProperties(value = {SecurityProperties.class})
@EnableWebSecurity
@ConditionalOnProperty(name = "enable", prefix = "agile.security", havingValue = "true")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnClass({AgileCacheManagerInterface.class, WebSecurityConfigurerAdapter.class, AuthenticationProvider.class})
public class SecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private final Set<String> immuneUrl;

    private final SecurityProperties securityProperties;

    public static final String ACCESS_SUCCESS = "SPRING_SECURITY_ACCESS_SUCCESS";
    private static String errorUrl;
    private static String successUrl;
    private static String logoutSuccessUrl;

    public static String getErrorUrl() {
        return errorUrl;
    }

    public static String getSuccessUrl() {
        return successUrl;
    }

    public static String getLogoutSuccessUrl() {
        return logoutSuccessUrl;
    }

    @Value("${agile.security.fail-forward-url}")
    public void setErrorUrl(String errorUrl) {
        SecurityAutoConfiguration.errorUrl = errorUrl;
    }

    @Value("${agile.security.success-forward-url}")
    public void setSuccessUrl(String successUrl) {
        SecurityAutoConfiguration.successUrl = successUrl;
    }

    @Value("${agile.security.success-logout-forward-url}")
    public void setLogoutSuccessUrl(String logoutSuccessUrl) {
        SecurityAutoConfiguration.logoutSuccessUrl = logoutSuccessUrl;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers(immuneUrl.toArray(new String[]{})).permitAll().anyRequest().authenticated()
                .and().logout().logoutUrl(securityProperties.getLoginOutUrl()).deleteCookies(securityProperties.getTokenHeader()).addLogoutHandler(tokenCleanLogoutHandler()).logoutSuccessHandler(new ForwardLogoutSuccessHandler(getLogoutSuccessUrl()))
                .and().exceptionHandling().accessDeniedPage(errorUrl)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).sessionFixation().none()
                .and().csrf().disable().httpBasic().disable()
                .addFilterAt(tokenFilter(), LogoutFilter.class);
    }

    @Bean
    LoginFilter loginFilter() {
        return new LoginFilter(securityProperties.getLoginUrl());
    }

    @Bean
    TokenFilter tokenFilter() {
        return new TokenFilter(immuneUrl, securityProperties);
    }

    @Bean
    ForwardLogoutSuccessHandler logoutHandler() {
        return new ForwardLogoutSuccessHandler(successUrl);
    }

    @Autowired
    public SecurityAutoConfiguration(SecurityProperties securityProperties) {
        this.immuneUrl = securityProperties.getExcludeUrl();
        this.immuneUrl.add("/static/**");
        this.immuneUrl.add("/favicon.ico");
        this.immuneUrl.add("/actuator/**");
        this.immuneUrl.add("/actuator/*");
        this.immuneUrl.add("actuator");
        this.immuneUrl.add("/jolokia");
        this.immuneUrl.add(securityProperties.getLoginUrl());

        this.securityProperties = securityProperties;
    }

    @Bean
    ForwardController forwardController(ErrorAttributes errorAttributes,
                                        ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new ForwardController(errorAttributes,
                errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }

    @Bean
    TokenCleanLogoutHandler tokenCleanLogoutHandler() {
        return new TokenCleanLogoutHandler();
    }

    @Bean
    ProviderManager providerManager(AuthenticationProvider... authenticationProvider){
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider(){
        return new JwtAuthenticationProvider();
    }
}
