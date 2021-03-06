package cloud.agileframework.security.filter.token;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.Version;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 佟盟
 * 日期 2019/3/20 19:52
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Data
public class TokenInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String token;
    private Date start;
    private Date end;
    private String ip;
    private Browser browser;
    private String browserVersion;
    private OperatingSystem os;
}
