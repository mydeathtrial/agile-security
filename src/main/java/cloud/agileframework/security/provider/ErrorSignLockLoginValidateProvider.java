package cloud.agileframework.security.provider;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.security.exception.LoginErrorLockException;
import cloud.agileframework.security.filter.login.ErrorSignInfo;
import cloud.agileframework.security.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

/**
 * @author 佟盟
 * 日期 2020/8/00026 20:08
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class ErrorSignLockLoginValidateProvider implements LoginValidateProvider {
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void validate(HttpServletRequest request, HttpServletResponse response, String username, String password) throws AuthenticationException {

        // 生成失败锁定标志
        ErrorSignInfo errorSignInfo = ErrorSignInfo.of(request, username, securityProperties.getErrorSign().getLockType());
        request.setAttribute(ErrorSignInfo.REQUEST_ATTR, errorSignInfo);

        // 验证登陆失败锁
        judgeLoginErrorLock(errorSignInfo);
    }

    /**
     * 验证失败锁定
     *
     * @param errorSignInfo 账户失败标识信息
     * @throws LoginErrorLockException 失败锁定异常
     */
    private void judgeLoginErrorLock(ErrorSignInfo errorSignInfo) throws LoginErrorLockException {
        if (!securityProperties.getErrorSign().isEnable()) {
            return;
        }

        AgileCache cache = securityProperties.getErrorSign().getCache();

        //已失败次数
        Integer errorCount = cache.get(errorSignInfo.getLockObject(), Integer.class);

        if (errorCount != null && errorCount >= securityProperties.getErrorSign().getMaxErrorCount()) {
            //锁定过期时间
            Duration lockTime = securityProperties.getErrorSign().getLockTime();
            boolean alwaysLock = lockTime.toMillis() <= 0;

            long minutes = lockTime.toMinutes();
            if (minutes == 0) {
                throw new LoginErrorLockException(alwaysLock ? "请联系管理员解锁" : lockTime.getSeconds() + "秒");
            } else {
                throw new LoginErrorLockException(alwaysLock ? "请联系管理员解锁" : minutes + "分钟");
            }
        }
    }
}
