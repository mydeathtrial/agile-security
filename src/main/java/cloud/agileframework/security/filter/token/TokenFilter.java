package cloud.agileframework.security.filter.token;

import cloud.agileframework.security.config.SecurityAutoConfiguration;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.util.TokenUtil;
import cloud.agileframework.spring.util.ParamUtil;
import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 登录验证码拦截器
 *
 * @author 佟盟 on 2017/9/27
 */
public class TokenFilter extends OncePerRequestFilter {
    private final AccessDeniedHandlerImpl failureHandler = new AccessDeniedHandlerImpl();
    ;
    private final List<RequestMatcher> matches;

    private final SecurityProperties securityProperties;

    public TokenFilter(Set<String> immuneUrl, SecurityProperties securityProperties) {
        matches = ServletUtil.coverRequestMatcher(immuneUrl.toArray(new String[]{}));
        failureHandler.setErrorPage(SecurityAutoConfiguration.getErrorUrl());
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        super.doFilterNestedErrorDispatch(request, response, filterChain);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            if (ServletUtil.matcherRequest(request, matches)) {
                filterChain.doFilter(request, response);
                return;
            }
            //获取令牌
            String token = ParamUtil.getInfo(request, securityProperties.getTokenHeader());

            //根据令牌---提取当前登录信息
            CurrentLoginInfo currentLoginInfo = LoginCacheInfo.getCurrentLoginInfo(token);

            //验证当前登陆用户信息合法性
            LoginCacheInfo.validateCacheDate(currentLoginInfo.getLoginCacheInfo());

            //账户信息赋给业务层
            SecurityContextHolder.getContext().setAuthentication(currentLoginInfo.getLoginCacheInfo().getAuthentication());

            //执行业务层程序
            filterChain.doFilter(request, response);

            //判断策略，复杂令牌时刷新token
            if (securityProperties.getTokenType() == SecurityProperties.TokenType.DIFFICULT) {
                String newToken = LoginCacheInfo.refreshToken(currentLoginInfo);
                TokenUtil.notice(request, response, newToken);
            }
        } catch (Exception e) {
            failureHandler.handle(request, response, new AccessDeniedException("令牌验证失败", e));
        }
    }
}
