package cloud.agileframework.security.provider;

/**
 * @author 佟盟
 * 日期 2019/10/11 14:24
 * 描述 帐号退出以后调用
 * @version 1.0
 * @since 1.0
 */
public interface LoginOutProcessorProvider {
    /**
     * 退出之后
     *
     * @param username 帐号
     * @param token    身份令牌
     */
    void after(String username, String token);
}
