package cloud.agileframework.security.properties;

/**
 * @author 佟盟
 * 日期 2019/3/15 12:21
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public enum LoginStrategy {

    /**
     * 单例-禁止替换
     */
    SINGLETON,
    /**
     * 多例
     */
    MORE,
    /**
     * 单例-替换上一个用户
     */
    SINGLETON_REPLACE;

    @Override
    public String toString() {
        return "login_strategy$$" + name();
    }
}
