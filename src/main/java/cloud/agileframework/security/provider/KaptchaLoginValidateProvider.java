package cloud.agileframework.security.provider;

import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.kaptcha.properties.KaptchaConfigProperties;
import cloud.agileframework.security.exception.VerificationCodeException;
import cloud.agileframework.security.exception.VerificationCodeExpire;
import cloud.agileframework.security.exception.VerificationCodeNon;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.spring.util.ParamUtil;
import cloud.agileframework.spring.util.RequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/8/00026 14:36
 * 描述 验证码验证
 * @version 1.0
 * @since 1.0
 */
public class KaptchaLoginValidateProvider implements LoginValidateProvider {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private KaptchaConfigProperties kaptchaConfigProperties;

    @Override
    public void validate(HttpServletRequest request, HttpServletResponse response, String username, String password) throws AuthenticationException {
        request = RequestWrapper.of(request);
        //获取用户名密码
        Map<String, Object> params = ((RequestWrapper) request).getInParam();
        String validateCode = ParamUtil.getInParam(params, securityProperties.getVerificationCode(), String.class);
        if (validateCode == null) {
            throw new VerificationCodeNon();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("正在登陆...[验证码：%s]", validateCode));
        }
        String codeToken = ParamUtil.getInfo(request, kaptchaConfigProperties.getTokenHeader());
        if (codeToken == null) {
            throw new VerificationCodeException();
        }
        Object cacheCodeToken = CacheUtil.get(codeToken);
        if (cacheCodeToken == null) {
            throw new VerificationCodeExpire();
        }
        if (!cacheCodeToken.toString().equalsIgnoreCase(validateCode)) {
            throw new VerificationCodeException(String.format("正确值:%s;输入值:%s", cacheCodeToken, validateCode));
        }
        Cookie cookie = new Cookie(kaptchaConfigProperties.getTokenHeader(), null);
        String cookiePath = request.getContextPath() + Constant.RegularAbout.SLASH;
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
