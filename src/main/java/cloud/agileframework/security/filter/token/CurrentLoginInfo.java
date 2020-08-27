package cloud.agileframework.security.filter.token;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 佟盟
 * 日期 2019/3/20 20:19
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Data
@AllArgsConstructor
public class CurrentLoginInfo {
    private Long sessionToken;
    private LoginCacheInfo loginCacheInfo;
}
