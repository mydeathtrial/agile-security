package cloud.agileframework.security.filter.login;

import cloud.agileframework.security.properties.LockType;
import cloud.agileframework.spring.util.ServletUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author 佟盟
 * 日期 2020/8/00026 19:16
 * 描述 错误登录信息
 * @version 1.0
 * @since 1.0
 */
@Builder
@Getter
public class ErrorSignInfo {
    public static final String REQUEST_ATTR = "AGILE_LOGIN_USERNAME";
    private final String lockObject;
    private final String ip;
    private final String sessionId;
    private final String account;
    private Date lockTime;
    private Date timeOut;

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    /**
     * 获取锁定对象
     *
     * @param request        请求
     * @param sourceUsername 账户
     * @return 锁定依据
     */
    public static ErrorSignInfo of(HttpServletRequest request, String sourceUsername, LockType[] lockTypes) {
        ErrorSignInfo.ErrorSignInfoBuilder builder = ErrorSignInfo.builder();

        StringBuilder lockObject = new StringBuilder();

        if (ArrayUtils.contains(lockTypes, LockType.IP)) {
            lockObject.append(ServletUtil.getCurrentRequestIP());
            builder.ip(ServletUtil.getRequestIP(request));
        }
        if (ArrayUtils.contains(lockTypes, LockType.SESSION_ID)) {
            lockObject.append(request.getSession().getId());
            builder.sessionId(request.getSession().getId());
        }
        if (ArrayUtils.contains(lockTypes, LockType.ACCOUNT)) {
            lockObject.append(sourceUsername);
            builder.account(sourceUsername);
        }

        return builder
                .lockObject(lockObject.toString())
                .build();
    }
}
