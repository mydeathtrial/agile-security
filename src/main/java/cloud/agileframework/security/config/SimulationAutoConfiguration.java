package cloud.agileframework.security.config;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.security.filter.simulation.SimulationFilter;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.properties.SimulationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * @author 佟盟
 * 日期 2020/8/00028 10:46
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties({SimulationProperties.class, SecurityProperties.class})
@ConditionalOnProperty(name = "enable", prefix = "agile.simulation")
public class SimulationAutoConfiguration implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    @Conditional(Is.class)
    public FilterRegistrationBean<SimulationFilter> simulationFilter() {
        logger.debug("完成初始化模拟账户过滤器");
        FilterRegistrationBean<SimulationFilter> simulationFilter = new FilterRegistrationBean<>();
        simulationFilter.setFilter(new SimulationFilter(simulationProperties));
        simulationFilter.addUrlPatterns("/*");
        simulationFilter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return simulationFilter;
    }

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private SimulationProperties simulationProperties;

    @Autowired(required = false)
    private UserDetailsManager userDetailsManager;

    @Override
    public void afterPropertiesSet() {
        if (securityProperties.isEnable()) {
            UserDetails user = ObjectUtil.to(simulationProperties.getUser(),
                    new TypeReference<>(simulationProperties.getUserClass()));
            if (user == null) {
                throw new RuntimeException("模拟账户数据user无法转换成目标userClass类，请仔细核对");
            }
            userDetailsManager.createUser(user);
        }
    }

    public static class Is implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            final ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            if (beanFactory == null) {
                return true;
            }
            Boolean need = context.getEnvironment()
                    .getProperty("agile.security.enable", Boolean.class, false);
            return !need;
        }
    }
}
