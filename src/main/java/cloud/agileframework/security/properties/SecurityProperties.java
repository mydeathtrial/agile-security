package cloud.agileframework.security.properties;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import cloud.agileframework.common.constant.Constant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.security")
@Setter
@Getter
public class SecurityProperties implements Serializable {
    private final String errorSignLockTimeCacheKey = "ErrorSignLockTime";
    private final String maxErrorCountCacheKey = "MaxErrorCount";
    /**
     * 开关
     */
    private boolean enable = true;
    /**
     * 排除的地址
     */
    private Set<String> excludeUrl;
    /**
     * 登录地址
     */
    private String loginUrl = "/login";
    /**
     * 登出地址
     */
    private String loginOutUrl = "/logout";
    /**
     * 验证码
     */
    private String verificationCode = "verification";
    /**
     * token密钥
     */
    private String tokenSecret = "23617641641";
    /**
     * token超时时间
     */
    private Duration tokenTimeout = Duration.ofMinutes(30);
    /**
     * token传递header名
     */
    private String tokenHeader = "AGILE_TOKEN";
    /**
     * token传输模式
     */
    private TransmissionMode[] tokenTransmissionMode = new TransmissionMode[]{TransmissionMode.HEADER};
    /**
     * 登录账号表单名
     */
    private String loginUsername = "username";
    /**
     * 登录密码表单名
     */
    private String loginPassword = "password";
    /**
     * token类型
     */
    private TokenType tokenType = TokenType.EASY;
    /**
     * 真实IP头名
     */
    private String realIpHeader = "X-Real-Ip";

    /**
     * token传输模式
     */
    public enum TransmissionMode {
        /**
         * cookie
         */
        COOKIE,
        /**
         * head传输
         */
        HEADER
    }

    /**
     * Token级别
     */
    public enum TokenType {
        /**
         * 容易
         */
        EASY,
        /**
         * 难
         */
        DIFFICULT
    }

    /**
     * 密码
     */
    private Password password = new Password();

    /**
     * 登陆
     */
    private ErrorSign errorSign = new ErrorSign();

    /**
     * 密码
     */
    @Data
    public static class Password implements Serializable {
        /**
         * 密码最低强度
         */
        private float strength = Constant.NumberAbout.FIVE;
        /**
         * 密码有效期
         */
        private Duration duration = Duration.ofDays(Constant.NumberAbout.THIRTY_ONE);
        /**
         * 密钥
         */
        private String aesKey = "idssinsightkey01";

        /**
         * 偏移量
         */
        private String aesOffset = "3612213421341234";

        /**
         * 算法模式
         */
        private String algorithmModel = "AES/CBC/PKCS5Padding";

        /**
         * 强度解析器配置
         */
        private Strength strengthConf = new Strength();

    }

    /**
     * 锁定类型
     */
    public enum LockType {
        // ip
        IP,
        // sessionId
        SESSION_ID,
        // 帐号
        ACCOUNT
    }

    /**
     * 强度权重配置
     */
    @Data
    public static class Strength implements Serializable {
        /**
         * 最大允许密码长度
         */
        private int maxLength;
        /**
         * 正则权重
         */
        private double weightOfRegex;
        /**
         * 关键字权重
         */
        private double weightOfKeyWord;
        /**
         * 正则配置
         */
        private List<WeightMap> weightOfRegexMap;
        /**
         * 关键字配置
         */
        private List<String> weightOfKeyWords;
    }

    /**
     * 正则权重映射
     */
    @Data
    public static class WeightMap implements Serializable {
        private String regex;
        private double weight;
    }

    /**
     * 登陆
     */
    @Data
    public static class ErrorSign implements Serializable {
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
        private Duration errorSignLockTime = Duration.ofMinutes(Constant.NumberAbout.TWO);
        /**
         * 登录失败计算超时
         */
        private Duration errorSignCountTimeout = Duration.ofMinutes(Constant.NumberAbout.TWO);
        /**
         * 过期是否锁定
         */
        private boolean lockForExpiration = true;
        /**
         * 锁定类型
         */
        private LockType[] lockType = new LockType[]{LockType.SESSION_ID};
    }

    public AgileCache getCache() {
        return CacheUtil.getCache(getTokenHeader());
    }
}
