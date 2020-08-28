package cloud.agileframework.security.properties;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.constant.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author 佟盟
 * 日期 2020/8/00028 11:07
 * 描述 失败登录
 * @version 1.0
 * @since 1.0
 */
@ConfigurationProperties(prefix = "agile.security.error-sign")
@Data
public class ErrorSignProperties implements Serializable {
    public AgileCache getCache() {
        return CacheUtil.getCache("ErrorSign");
    }

    private boolean enable = true;
    /**
     * 最大登录失败次数
     */
    private int maxErrorCount = Constant.NumberAbout.FIVE;
    /**
     * 登录失败锁定时间
     */
    private Duration lockTime = Duration.ofMinutes(Constant.NumberAbout.TWO);
    /**
     * 登录失败计算超时
     */
    private Duration countTimeout = Duration.ofMinutes(Constant.NumberAbout.TWO);
    /**
     * 过期是否锁定
     */
    private boolean lockForExpiration = true;
    /**
     * 锁定类型
     */
    private LockType[] lockType = new LockType[]{LockType.SESSION_ID};
}
