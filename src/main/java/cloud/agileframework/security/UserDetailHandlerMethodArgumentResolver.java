package cloud.agileframework.security;

import cloud.agileframework.spring.util.SecurityUtil;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author 佟盟
 * 日期 2020/6/1 14:17
 * 描述 Agile参数解析器
 * @version 1.0
 * @since 1.0
 */
public class UserDetailHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return UserDetails.class.isAssignableFrom(methodParameter.getParameterType());
    }

    @Override
    @Nullable
    public Object resolveArgument(@Nullable MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return SecurityUtil.currentUser();
    }
}
