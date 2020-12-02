package cloud.agileframework.security.util;

import cloud.agileframework.common.util.date.DateUtil;
import cloud.agileframework.common.util.rsa.RSAUtil;
import cloud.agileframework.security.filter.token.LoginCacheInfo;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.properties.TransmissionMode;
import cloud.agileframework.spring.util.BeanUtil;
import cloud.agileframework.spring.util.IdUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 佟盟 on 2018/7/4
 */
public class TokenUtil {
    /**
     * 存储账号信息的缓存key
     */
    public static final String AUTHENTICATION_USER_NAME = "AUTHENTICATION_USER_NAME";
    /**
     * 存储当前会话口令
     */
    public static final String AUTHENTICATION_SESSION_TOKEN = "AUTHENTICATION_SESSION_TOKEN";

    private static final String AUTHENTICATION_CREATE_TIME = "created";

    private static final SecurityProperties securityProperties = BeanUtil.getBean(SecurityProperties.class);

    private static PrivateKey privateKey;

    private static PublicKey publicKey;
    /**
     * token令牌加解密的密钥对
     */
    private static final String KEY_PAIR_CACHE_KEY = "$AGILE_SECURITY_RSA_KEY_PAIR$";
    private static final String RSA = "RSA";
    private static final int KEY_SIZE = 2048;

    @SneakyThrows
    private static void init() {
        String text = LoginCacheInfo.getCache().get(KEY_PAIR_CACHE_KEY, String.class);
        KeyPair keyPair = RSAUtil.toKeyPair(text);
        if (keyPair == null) {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA);
            keyPairGen.initialize(KEY_SIZE);
            keyPair = keyPairGen.generateKeyPair();
            LoginCacheInfo.getCache().put(KEY_PAIR_CACHE_KEY, RSAUtil.toString(keyPair));
        }
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public static PrivateKey getPrivateKey() {
        if (privateKey == null) {
            init();
        }
        return privateKey;
    }

    public static PublicKey getPublicKey() {
        if (publicKey == null) {
            init();
        }
        return publicKey;
    }

    /**
     * 根据 TokenDetail 生成 Token
     */
    public static String generateToken(String username, Long sessionToken, Date timeout) {
        final int length = 4;
        Map<String, Object> claims = new HashMap<>(length);
        claims.put(AUTHENTICATION_USER_NAME, username);
        claims.put(AUTHENTICATION_SESSION_TOKEN, sessionToken);
        claims.put(AUTHENTICATION_CREATE_TIME, DateUtil.getCurrentDate());
        return generateToken(claims, timeout);
    }


    public static String generateToken(Map<String, Object> claims, Date timeout) {
        return Jwts.builder()
                .setIssuer("agile")
                .setIssuedAt(DateUtil.getCurrentDate())
                .setSubject("Login")
                .setAudience("System Users")
                .setNotBefore(DateUtil.getCurrentDate())
                .setId(Long.toString(IdUtil.generatorId()))
                .setClaims(claims)
                .setExpiration(timeout)
                .signWith(SignatureAlgorithm.RS512, getPrivateKey())
                .compact();

    }

    /**
     * 通知前端
     *
     * @param response 响应
     * @param token    令牌
     */
    @SneakyThrows
    public static void notice(HttpServletRequest request, HttpServletResponse response, String token) {
        TransmissionMode[] modes = securityProperties.getTokenTransmissionMode();
        for (TransmissionMode mode : modes) {
            switch (mode) {
                case COOKIE:
                    response.addCookie(new Cookie(securityProperties.getTokenHeader(), token));
                    break;
                case HEADER:
                    response.setHeader(securityProperties.getTokenHeader(), token);
                    break;
                default:
            }
        }
        request.setAttribute(securityProperties.getTokenHeader(), token);
    }

    /**
     * 解析 token 的主体 Claims
     */
    public static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getPublicKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (ObjectUtils.isEmpty(claims)) {
            return false;
        }
        return claims.getExpiration().after(DateUtil.getCurrentDate());
    }
}
