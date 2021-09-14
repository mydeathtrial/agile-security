package cloud.agileframework.security.config;

import cloud.agileframework.security.filter.login.CustomRememberMeServices;
import cloud.agileframework.security.filter.login.CustomerUserDetailsService;
import cloud.agileframework.security.filter.login.InMemoryUserDetailsServiceImpl;
import cloud.agileframework.security.provider.CompleteFormLoginValidateProvider;
import cloud.agileframework.security.provider.ErrorSignLockLoginValidateProvider;
import cloud.agileframework.security.provider.KaptchaLoginValidateProvider;
import cloud.agileframework.security.provider.LoginStrategyLoginValidateProvider;
import cloud.agileframework.security.provider.LoginValidateProvider;
import cloud.agileframework.security.provider.PasswordLoginValidateProvider;
import cloud.agileframework.security.provider.PasswordProvider;
import cloud.agileframework.security.provider.SecurityResultProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.authentication.RememberMeServices;

/**
 * @author 佟盟
 * 日期 2020/8/00024 17:27
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class SecurityAboutConfiguration {
    @Bean
    @ConditionalOnMissingBean(PasswordProvider.class)
    PasswordProvider passwordProvider() {
        return ciphertext -> ciphertext;
    }

    @Bean
    @ConditionalOnMissingBean(SecurityResultProvider.class)
    public SecurityResultProvider securityResultHandler() {
        return new SecurityResultProvider() {
        };
    }

    @Bean
    @Order(4)
    public LoginStrategyLoginValidateProvider loginStrategyLoginValidateProvider() {
        return new LoginStrategyLoginValidateProvider();
    }

    /**
     * 验证码验证器
     */
    @Bean
    @Order(3)
    @ConditionalOnClass(KaptchaLoginValidateProvider.class)
    @ConditionalOnProperty(name = "enable", prefix = "agile.kaptcha", matchIfMissing = true)
    public LoginValidateProvider kaptchaLoginValidateProvider() {
        return new KaptchaLoginValidateProvider();
    }

    /**
     * 表单完整性验证器
     */
    @Bean
    @Order(2)
    public CompleteFormLoginValidateProvider completeFormLoginValidateProvider() {
        return new CompleteFormLoginValidateProvider();
    }

    /**
     * 登陆失败锁验证器
     */
    @Bean
    @Order(1)
    public ErrorSignLockLoginValidateProvider errorSignLockLoginValidateProvider() {
        return new ErrorSignLockLoginValidateProvider();
    }

    /**
     * 密码验证器
     */
    @Bean
    @Order(1)
    public PasswordLoginValidateProvider passwordLoginValidateProvider() {
        return new PasswordLoginValidateProvider();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        return new CustomRememberMeServices();
    }

    @Bean
    @ConditionalOnMissingBean(CustomerUserDetailsService.class)
    public InMemoryUserDetailsServiceImpl inMemoryUserDetailsService() {
        return new InMemoryUserDetailsServiceImpl();
    }
}
