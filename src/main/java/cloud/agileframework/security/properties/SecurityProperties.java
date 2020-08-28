package cloud.agileframework.security.properties;

import cloud.agileframework.cache.support.AgileCache;
import cloud.agileframework.cache.util.CacheUtil;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.time.Duration;
import java.util.Set;

/**
 * @author by 佟盟 on 2018/2/1
 */
@ConfigurationProperties(prefix = "agile.security")
@Setter
@Getter
public class SecurityProperties implements Serializable, InitializingBean {
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
    private TransmissionMode[] tokenTransmissionMode = new TransmissionMode[]{TransmissionMode.COOKIE};
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
     * 密码
     */
    private PasswordProperties password = new PasswordProperties();

    /**
     * 登陆
     */
    private ErrorSignProperties errorSign = new ErrorSignProperties();

    private String failForwardUrl = "/fail";

    /**
     * 失败重定向地址
     */
    private String successForwardUrl = "/success";

    /**
     * 登录成功重定向地址
     */
    private String successLogoutForwardUrl = "/logout-success";

    /**
     * 退出成功重定向地址
     */
    public AgileCache getCache() {
        return CacheUtil.getCache(getTokenHeader());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (excludeUrl == null) {
            excludeUrl = Sets.newHashSet();
        }
        excludeUrl.add(loginUrl);
        excludeUrl.add("/static/**");
        excludeUrl.add("/favicon.ico");
        excludeUrl.add("/actuator/**");
        excludeUrl.add("/actuator/*");
        excludeUrl.add("actuator");
        excludeUrl.add("/jolokia");
    }
}
