package cloud.agileframework.security.provider;

import cloud.agileframework.security.exception.NoCompleteFormSign;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 佟盟
 * 日期 2020/8/00026 20:13
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class CompleteFormLoginValidateProvider implements LoginValidateProvider {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void validate(HttpServletRequest request, HttpServletResponse response, String username, String password) throws AuthenticationException {
        // 验证表单完整性
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new NoCompleteFormSign();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("正在登陆...[账号：%s][密码：%s]", username, password));
        }
    }
}
