package cloud.agileframework.security.controller;

import cloud.agileframework.security.filter.logout.TokenCleanLogoutHandler;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.provider.SecurityResultProvider;
import cloud.agileframework.security.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 佟盟
 * 日期 2020/8/00025 16:12
 * 描述 认证结果处理
 * @version 1.0
 * @since 1.0
 */
@Controller
public class ForwardController extends AbstractErrorController {
    @Autowired
    private SecurityResultProvider securityResultProvider;

    @Autowired
    private SecurityProperties securityProperties;

    public ForwardController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
    }

    @RequestMapping({"${agile.security.fail-forward-url:/fail}", "${server.error.path:${error.path:/error}}"})
    public Object error(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        handleStatus(request, response);
        handleToken(request, response);

        return handleException(request, response);
    }

    public void handleStatus(HttpServletRequest request, HttpServletResponse response) {
        Object statusCode = request.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE);
        if (statusCode instanceof Integer) {
            response.setStatus((Integer) statusCode);
        }
    }

    public Object handleException(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        Object e = request.getAttribute(WebAttributes.ACCESS_DENIED_403);

        if (!(e instanceof Throwable)) {
            e = request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
        if (!(e instanceof Throwable)) {
            e = request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
        }
        if (e instanceof Throwable) {
            return securityResultProvider.accessException(request, response, cause((Throwable) e));
        }

        return null;
    }

    public void handleToken(HttpServletRequest request, HttpServletResponse response) {
        Object token = request.getAttribute(securityProperties.getTokenHeader());
        if (token instanceof String) {
            TokenUtil.notice(request, response, (String) token);
        }
    }

    @ResponseBody
    @RequestMapping("${agile.security.success-forward-url:/success}")
    public Object success(HttpServletRequest request, HttpServletResponse response) {
        return securityResultProvider.loginSuccess(request,
                response,
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication());
    }

    @ResponseBody
    @RequestMapping("${agile.security.success-logout-forward-url:/logout-success}")
    public Object logoutSuccess(HttpServletRequest request, HttpServletResponse response) {
        return securityResultProvider.logoutSuccess(request,
                response,
                (String) request.getAttribute(TokenCleanLogoutHandler.LOGOUT_USERNAME),
                (String) request.getAttribute(TokenCleanLogoutHandler.LOGOUT_TOKEN));
    }


    @Override
    @Deprecated
    public String getErrorPath() {
        return null;
    }

    private Throwable cause(Throwable throwable) {
        final Throwable cause = throwable.getCause();
        if (cause == null) {
            return throwable;
        }
        return cause(cause);
    }
}
