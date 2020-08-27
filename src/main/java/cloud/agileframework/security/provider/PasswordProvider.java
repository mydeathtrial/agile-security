package cloud.agileframework.security.provider;

/**
 * @author 佟盟
 * 日期 2020/8/00025 18:16
 * 描述 密码解密，用于前后端密文传输扩展使用
 * @version 1.0
 * @since 1.0
 */
public interface PasswordProvider {
    /**
     * 解密
     *
     * @param ciphertext 密文
     * @return 明文
     */
    String decrypt(String ciphertext);
}
