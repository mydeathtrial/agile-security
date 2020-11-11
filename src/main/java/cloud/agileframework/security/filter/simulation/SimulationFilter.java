package cloud.agileframework.security.filter.simulation;

import cloud.agileframework.common.util.clazz.TypeReference;
import cloud.agileframework.common.util.object.ObjectUtil;
import cloud.agileframework.security.properties.SimulationProperties;
import cloud.agileframework.spring.util.SecurityUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 佟盟
 * 日期 2020/8/00028 10:38
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class SimulationFilter extends OncePerRequestFilter {
    private final SimulationProperties simulationProperties;

    public SimulationFilter(SimulationProperties simulationProperties) {
        this.simulationProperties = simulationProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null && simulationProperties.isEnable()) {
            UserDetails user = ObjectUtil.to(simulationProperties.getUser(),
                    new TypeReference<>(simulationProperties.getUserClass()));
            if (user == null) {
                throw new RuntimeException("模拟账户数据user无法转换成目标userClass类，请仔细核对");
            }
            authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        SecurityUtil.setCurrentUser(request, authentication);
        filterChain.doFilter(request, response);
    }
}
