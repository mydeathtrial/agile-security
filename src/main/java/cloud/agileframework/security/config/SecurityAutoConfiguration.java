package cloud.agileframework.security.config;

import cloud.agileframework.cache.support.AgileCacheManagerInterface;
import cloud.agileframework.security.controller.ForwardController;
import cloud.agileframework.security.filter.login.JwtAuthenticationProvider;
import cloud.agileframework.security.filter.login.LoginFilter;
import cloud.agileframework.security.filter.logout.TokenCleanLogoutHandler;
import cloud.agileframework.security.filter.simulation.SimulationFilter;
import cloud.agileframework.security.filter.token.TokenFilter;
import cloud.agileframework.security.properties.ErrorSignProperties;
import cloud.agileframework.security.properties.PasswordProperties;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.properties.StrengthProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.web.authentication.logout.ForwardLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.jackson2.WebJackson2Module;

import java.util.stream.Collectors;

/**
 * @author 佟盟 on 2017/9/26
 */
@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ImportAutoConfiguration(SecurityAboutConfiguration.class)
@EnableConfigurationProperties(value = {SecurityProperties.class, PasswordProperties.class, StrengthProperties.class, ErrorSignProperties.class})
@EnableWebSecurity
@ConditionalOnProperty(name = "enable", prefix = "agile.security", matchIfMissing = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnClass({AgileCacheManagerInterface.class, WebSecurityConfigurerAdapter.class, AuthenticationProvider.class})
public class SecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired(required = false)
    private SimulationFilter simulationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers(securityProperties.getExcludeUrl().toArray(new String[]{})).permitAll().anyRequest().authenticated()
                .and().logout().logoutUrl(securityProperties.getLoginOutUrl()).deleteCookies(securityProperties.getTokenHeader()).addLogoutHandler(tokenCleanLogoutHandler()).logoutSuccessHandler(new ForwardLogoutSuccessHandler(securityProperties.getSuccessLogoutForwardUrl()))
                .and().exceptionHandling().accessDeniedPage(securityProperties.getFailForwardUrl())
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).sessionFixation().migrateSession()
                .and().csrf().disable().httpBasic().disable()
                .addFilterAt(tokenFilter(), LogoutFilter.class);

        if (simulationFilter != null) {
            http.addFilterBefore(simulationFilter, TokenFilter.class);
        }
    }

    @Bean
    LoginFilter loginFilter() {
        return new LoginFilter(securityProperties.getLoginUrl());
    }

    @Bean
    TokenFilter tokenFilter() {
        return new TokenFilter();
    }

    @Bean
    ForwardLogoutSuccessHandler logoutHandler() {
        return new ForwardLogoutSuccessHandler(securityProperties.getSuccessForwardUrl());
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
    ProviderManager providerManager(AuthenticationProvider... authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider();
    }

    /**
     * jackson2正反序列化配置
     */
    @Bean
    CoreJackson2Module coreJackson2Module() {
        return new CoreJackson2Module();
    }

    @Bean
    WebJackson2Module webJackson2Module() {
        return new WebJackson2Module();
    }
}
