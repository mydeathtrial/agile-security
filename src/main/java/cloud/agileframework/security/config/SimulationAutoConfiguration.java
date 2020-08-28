package cloud.agileframework.security.config;

import cloud.agileframework.security.filter.simulation.SimulationFilter;
import cloud.agileframework.security.properties.SimulationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 佟盟
 * 日期 2020/8/00028 10:46
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(SimulationProperties.class)
@ConditionalOnProperty(name = "enable", prefix = "agile.simulation")
public class SimulationAutoConfiguration {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public SimulationFilter simulationFilter() {
        logger.debug("完成初始化模拟账户过滤器");
        return new SimulationFilter();
    }
}
