package cloud.agileframework.security.filter.token;

import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.properties.TokenType;
import cloud.agileframework.security.util.TokenUtil;
import cloud.agileframework.spring.util.ParamUtil;
import cloud.agileframework.spring.util.SecurityUtil;
import cloud.agileframework.spring.util.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

/**
 * 登录验证码拦截器
 *
 * @author 佟盟 on 2017/9/27
 */
public class TokenFilter extends OncePerRequestFilter {

    private final AccessDeniedHandlerImpl failureHandler = new AccessDeniedHandlerImpl();
    private List<RequestMatcher> matches;
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        matches = ServletUtil.coverRequestMatcher(securityProperties.getExcludeUrl().toArray(new String[]{}));
        failureHandler.setErrorPage(securityProperties.getFailForwardUrl());
    }

    @Override
    protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        super.doFilterNestedErrorDispatch(request, response, filterChain);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            // 判断模拟账户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof UsernamePasswordAuthenticationToken || ServletUtil.matcherRequest(request, matches)) {
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
            final Authentication currentAuthentication = currentLoginInfo.getLoginCacheInfo().getAuthentication();

            //请求信息中传递账户信息，用于后续过滤器使用
            SecurityUtil.setCurrentUser(request,currentAuthentication);

            SecurityContextHolder.getContext().setAuthentication(currentAuthentication);

            //执行业务层程序
            filterChain.doFilter(request, response);

            //判断策略，复杂令牌时刷新token
            if (securityProperties.getTokenType() == TokenType.DIFFICULT) {
                String newToken = LoginCacheInfo.refreshToken(currentLoginInfo);
                TokenUtil.notice(request, response, newToken);
            }

        } catch (Exception e) {
            failureHandler.handle(request, response, new AccessDeniedException("令牌验证失败", e));
        }
    }
}
