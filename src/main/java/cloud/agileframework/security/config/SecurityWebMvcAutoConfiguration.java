package cloud.agileframework.security.config;

import cloud.agileframework.security.UserDetailHandlerMethodArgumentResolver;
import cloud.agileframework.security.event.MenuEventListener;
import cloud.agileframework.security.util.MenuUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author 佟盟
 * 日期 2020/8/00028 18:44
 * 描述 mvc的参数解析器
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class SecurityWebMvcAutoConfiguration implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userDetailHandlerMethodArgumentResolver());
    }

    @Bean
    public UserDetailHandlerMethodArgumentResolver userDetailHandlerMethodArgumentResolver() {
        return new UserDetailHandlerMethodArgumentResolver();
    }


    @Bean
    @ConditionalOnProperty(name = "enable", prefix = "agile.menu")
    MenuEventListener menuEventListener(ApplicationEventPublisher eventPublisher) {
        MenuUtil.setEventPublisher(eventPublisher);
        return new MenuEventListener();
    }
}
