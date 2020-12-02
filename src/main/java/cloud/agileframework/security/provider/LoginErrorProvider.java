package cloud.agileframework.security.provider;

import cloud.agileframework.security.filter.login.ErrorSignInfo;

/**
 * @author 佟盟
 * 日期 2020-11-12 16:36
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public interface LoginErrorProvider {
    /**
     * 登录失败锁定动作
     *
     * @param errorSignInfo 错误信息
     */
    void lock(ErrorSignInfo errorSignInfo);
}
