# agile-security : 权限认证组件
该组件是在spring-security基础上，做了大量的定制化开发，使其支持已最简单的方式应用于我们的系统当中。
并且最大化遵循spring-security原生的设计思路，避免重复不必要的设计。设计过程中预留了相当多的可扩展接口，最大限度支持
二次开发。组件特色是在前后端分离场景下做了大量支持，默认使用缓存方式做账户信息存储。
----
[![spring-security](https://img.shields.io/badge/spring--security-LATEST-green)](https://img.shields.io/badge/spring--security-LATEST-green)
[![](https://img.shields.io/badge/build-maven-green)](https://img.shields.io/badge/build-maven-green)
## 它有什么作用

* **JWT+自动令牌延时+主动失效**
token由JWT方式生成，并通过缓存组件agile-cache为其增加了令牌自动延时与主动失效功能

* **可定制令牌传输途径**
内置响应头（header）与cookie两种令牌传输方式，可以根据实际场景的安全级别，调整令牌传输途径。

* **动态令牌**
提供简单、复杂两种令牌生成策略，默认简单策略为登陆到退出时间段内共享一个有效令牌。复杂策略为每次前后端通信均采用新令牌，并将上一次
通信令牌失效，确保每次前后端通信令牌唯一。但复杂令牌需前端做响应支持，当出现某一次令牌传输失败，有可能面临后续通信全部视为非法请求
的情况，所以复杂策略在没有确保前后端高度配合的情况下，一般不建议使用

* **为账户定制单独的登录测录**
这里的登录策略分为
```markdown
单例（同一时间同一帐号系统中只能存活一个有效登录）
多例（同一时间同一帐号系统中只能存活一个有效登录）
单点（同一帐号，在agile-security微服务或集群中，仅需登录一次）
```

* **可定制**
```markdown
`cloud.agileframework.security.provider`包下提供多扩展点（钩子函数），如：
`cloud.agileframework.security.provider.LoginValidateProvider`登录验证点，当产生帐号登录事件后，会触发该扩展点，且提供前端输入的帐号、密码、请求与响应等信息，扩展点通过抛出`AuthenticationException`类异常
进行非法登录识别
`cloud.agileframework.security.provider.PasswordProvider`密码解密点，当产生帐号登录事件后，会触发一次该扩展点，提供前端传输到服务端的密码，一般用于前后端密码约定密码加密，防止密码泄露的场景当中
`cloud.agileframework.security.provider.SecurityResultProvider`认证结果视图处理点，当发生登录成功、失败、权限认证失败等事件时，会触发该扩展点，提供相应的如异常、帐号信息等必要信息，用于制定符合自己需要的响应报文以及结果处理
`cloud.agileframework.security.provider.LogoutProcessorProvider`退出点，当产生帐号退出事件后，会触发该扩展点，且提供退出帐号及令牌信息
```
* **持久化数据**
默认提供内存方式账户数据持久化，开发者可根据实际需要向spring
容器中注入以下两类接口，实现对自定义持久化方式，如使用MySQL存储、为账户信息扩展邮箱、住址等等定制化需求
```
`cloud.agileframework.security.filter.login.CustomerUserDetails`账户信息结构
`cloud.agileframework.security.filter.login.CustomerUserDetailsService` 账户数据操作
```
* **集群、分布式**
组件认证是采用`无状态化服务`将关键认证数据存储于缓存，其中缓存部分由`agile-cache`提供，当分布式或集群场景中时，可将`agile-cache`配置为`redis`等三方缓存方式，即可实现无状态化服务方式认证。

* **验证码**
验证码由agile系列`spring-boot-starter-kaptcha`组件提供，并内置验证码组件开关以及样式等元数据方式配置。该验证码同样支持分布式/集群等方式认证，避免验证码颁发与验证不在同一服务器时无法验证的问题。

* **密码强度校验**
工具`cloud.agileframework.security.util.PasswordUtil`中提供了对密码强度的校验，并且支持扩展校验规则以及规则权重。校验结果为百分制，通过判断如AABB、ABCABC、键盘连续字母、数字、关键词、长度、字符种类等等诸多规则，结合响应规则权重，判断密码强度。

* **模拟账户**
可以通过提供json形式的模拟帐号信息，用于开发测试，

* **失败次数限制**
支持超过登录失败次数后根据登录限制锁，锁定登录的能力。并且登录失败计数间隔、锁定时长等等均可配。

* **登录限制锁**
内置三种登录限制对象，可单独或混合使用，如锁定指定ip下的指定浏览器会话；指定ip下的指定帐号等等。
```
    浏览器会话（sessionId）
    帐号（客户端请求登录的帐号）
    ip（客户端ip）
```
-------
## 快速入门
开始你的第一个项目是非常容易的。

#### 步骤 1: 下载包
您可以从[最新稳定版本]下载包(https://github.com/mydeathtrial/agile-security/releases).
该包已上传至maven中央仓库，可在pom中直接声明引用

以版本agile-security-1.0.jar为例。
#### 步骤 2: 添加maven依赖
```xml
<dependency>
    <groupId>cloud.agileframework</groupId>
    <artifactId>agile-security</artifactId>
    <version>1.0</version>
</dependency>
```
#### 步骤 3: 配置开关
```
agile.security.enable=true
```
#### 步骤 4: 登录/退出
+ 默认登录地址`/login?username=admin&password=password`
+ 默认退出地址`/logout`

## 配置说明
```properties
# 认证组件开关，该开关不限制模拟账户功能
agile.security.enable=true
# 排除认证地址
agile.security.exclude-url=${agile.kaptcha.url},/swagger-ui.html,/webjars/springfox-swagger-ui/*,/v2/api-docs,/swagger-resources/**,/api/password-find
# 退出地址
agile.security.login-out-url=/api/logout
# 登录地址
agile.security.login-url=/api/login
# 登录时前端提交的帐号属性key
agile.security.login-username=username
# 登录时前端提交的密码属性key
agile.security.login-password=password
# 验证码会话令牌key，用于分布式验证码支持，一般存于cookies
agile.security.verification-code=authCode
# 身份令牌过期时间，过期从最后一次服务器接收请求开始计算
agile.security.token-timeout=30m
# 身份令牌存储key，一般存于cookies或header中
agile.security.token-header=authToken
# 身份令牌传输途径，支持cookie、header头信息，可配置多个
agile.security.token-transmission-mode=cookie,header
# 身份令牌加密盐值
agile.security.token-secret=23617641641
# 身份令牌策略，easy（简单方式，不刷新），difficult（复杂方式，每次通信后刷新身份令牌）
agile.security.token-type=easy
# 客户端真实IP，请求头传输参数名
agile.security.real-ip-header=X-Real-Ip

# 密码强度
# 密码最低强度（暂未使用）
agile.security.password.strength=5
# 密码有效期 （暂未使用）
agile.security.password.duration=31d
# 密码传输密钥 （暂未使用）
agile.security.password.key=167223764989834
# 密码传输偏移量 （暂未使用）
agile.security.password.offset=3612213421341234
# 密码传输算法模式 （暂未使用）
agile.security.password.algorithm-model=AES/CBC/PKCS5Padding

# 密码强度检测配置
# 密码强度关键字匹配策略
agile.security.password.strength-conf.weight-of-key-words=password,iloveyou,sunshine,1314,520,a1b2c3,admin
# 密码强度关键字匹配策略整体权重
agile.security.password.strength-conf.weight-of-key-word=0.35
# 密码强度正则匹配策略及权重
agile.security.password.strength-conf.weight-of-regex-map[0].regex=(?:([\\da-zA-Z])\\1{2,})
agile.security.password.strength-conf.weight-of-regex-map[0].weight=0.4
agile.security.password.strength-conf.weight-of-regex-map[1].regex=(?:([\\da-zA-Z])\\1+){2,}
agile.security.password.strength-conf.weight-of-regex-map[1].weight=0.15
agile.security.password.strength-conf.weight-of-regex-map[2].regex=([\\da-zA-Z]{2,})\\1+
agile.security.password.strength-conf.weight-of-regex-map[2].weight=0.12
agile.security.password.strength-conf.weight-of-regex-map[3].regex=((?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){2,}+\\d)|((?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){2,}+\\d)
agile.security.password.strength-conf.weight-of-regex-map[3].weight=0.2
agile.security.password.strength-conf.weight-of-regex-map[4].regex=((?:a(?=b)|b(?=c)|c(?=d)|d(?=e)|e(?=f)|f(?=g)|g(?=h)|h(?=i)|i(?=j)|j(?=k)|k(?=l)|l(?=m)|m(?=n)|n(?=o)|o(?=p)|p(?=q)|q(?=r)|r(?=s)|s(?=t)|t(?=u)|u(?=v)|v(?=w)|w(?=x)|x(?=y)|y(?=z)){2,}+[a-z])|((?:A(?=B)|B(?=C)|C(?=D)|D(?=E)|E(?=F)|F(?=G)|G(?=H)|H(?=I)|I(?=J)|J(?=K)|K(?=L)|L(?=M)|M(?=N)|N(?=O)|O(?=P)|P(?=Q)|Q(?=R)|R(?=S)|S(?=T)|T(?=U)|U(?=V)|V(?=W)|W(?=X)|X(?=Y)|Y(?=Z)){2,}+[A-Z])
agile.security.password.strength-conf.weight-of-regex-map[4].weight=0.18
agile.security.password.strength-conf.weight-of-regex-map[5].regex=((?:q(?=w)|w(?=e)|e(?=r)|r(?=t)|t(?=y)|y(?=u)|u(?=i)|i(?=o)|o(?=p)){2,}+[a-z])|((?:Q(?=W)|W(?=E)|E(?=R)|R(?=T)|T(?=Y)|Y(?=U)|U(?=I)|I(?=O)|O(?=P)){2,}+[A-Z])|((?:a(?=s)|s(?=d)|d(?=f)|f(?=g)|g(?=h)|h(?=j)|j(?=k)|k(?=l)){2,}+[a-z])|((?:A(?=S)|S(?=D)|D(?=F)|F(?=G)|G(?=H)|H(?=J)|J(?=K)|K(?=L)){2,}+[A-Z])|((?:z(?=x)|x(?=c)|c(?=v)|v(?=b)|b(?=n)|n(?=m)){2,}+[a-z])|((?:Z(?=X)|X(?=C)|C(?=V)|V(?=B)|B(?=N)|N(?=M)){2,}+[A-Z])
agile.security.password.strength-conf.weight-of-regex-map[5].weight=0.1
agile.security.password.strength-conf.weight-of-regex-map[6].regex=((((19|20)\\d{2})-(0?[13-9]|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))
agile.security.password.strength-conf.weight-of-regex-map[6].weight=0.3
# 密码强度正则匹配策略整体权重
agile.security.password.strength-conf.weight-of-regex=0.65
# 长度策略权重最大得分
agile.security.password.strength-conf.max-length=32

# 登录失败限制配置
# 登录失败限制开关
agile.security.error-sign.enable=true
# 过期是否锁定 （暂未使用）
agile.security.error-sign.lock-for-expiration=true
# 最大失败次数，超过此次数后的登录行为将触发限制登录
agile.security.error-sign.max-error-count=5
# 限制登录时间
agile.security.error-sign.lock-time=3m
# 登录失败计数器间隔
agile.security.error-sign.count-timeout=1m
# 登录失败限制对象，ip（客户端ip）、account（登录帐号）、session_id（浏览器会话标识），可结合使用，以限制同ip下同账户同浏览器会话限制为例
agile.security.error-sign.lock-type=account,ip,session_id

# 认证组件结果处理请求转发地址
# 登录失败、权限验证失败转发地址
agile.security.fail-forward-url=/fail
# 登录成功转发地址
agile.security.success-forward-url=/success
# 退出成功地址
agile.security.success-logout-forward-url=/logout-success

# 模拟账号
# 模拟账号json数据
agile.simulation.user={"username":"admin2","password":"$2a$04$H5Zj6JmtZRyyrVKKMsJmO.txNXcRQNWxo5C.d0KoijnlqCbGdi0fq","enabled":true,"accountNonExpired":true,"accountNonLocked":false,"credentialsNonExpired":true,"authorities":[],"loginStrategy":"MORE"}
# 模拟账号信息映射java类型
agile.simulation.user-class=cloud.agileframework.security.filter.login.InMemoryUserDetails
# 模拟开关
agile.simulation.enable=false

# 验证码，其余验证码配置请参考https://github.com/mydeathtrial/spring-boot-starter-kaptcha
# 验证码组件开关
agile.kaptcha.enable=false
# 验证码地址
agile.kaptcha.url=/code
```

## 深度定制
#### 定制持久化方式
```
以JPA类框架为例，帐号信息的ORM映射类需要符合cloud.agileframework.security.filter.login.CustomerUserDetails接口，接口中约束了账户信息必要的帐号、密码、权限集等信息。
持久层化操作工具（如xxxService）应符合cloud.agileframework.security.filter.login.CustomerUserDetailsService接口，接口中约束账户信息持久化操作的必要方法，如创建、
更新、删除、判断、加载等等持久化能力。

组件中内置了以内存方式持久化的实现方式，可以直接参考以下源码：
账户信息：cloud.agileframework.security.filter.login.InMemoryUserDetails
持久化能力：cloud.agileframework.security.filter.login.InMemoryUserDetailsServiceImpl
```

#### 定制化登录验证
```
自定义验证提供者应该遵循接口cloud.agileframework.security.provider.LoginValidateProvider，组件将在登录事件发生时调用登录验证提供者提供的验证能力。组件
内置了表单完整度（CompleteFormLoginValidateProvider）、失败限制（ErrorSignLockLoginValidateProvider）、验证码（KaptchaLoginValidateProvider）、
登录策略（LoginStrategyLoginValidateProvider）四种登录验证提供者

以表单完整度为例，将其注入到spring容器，组件即可识别：
public class CompleteFormLoginValidateProvider implements LoginValidateProvider {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void validate(HttpServletRequest request, HttpServletResponse response, String username, String password) throws AuthenticationException {
        // 验证表单完整性
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new NoCompleteFormSign();
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("正在登陆...[账号：%s][密码：%s]", username, password));
        }
    }
}
```

#### 定制化密码密文传输
```
定制密码密文传输解密提供者，需要遵循接口cloud.agileframework.security.provider.PasswordProvider
组件会在登录验证之前调用定制密码密文传输解密提供者，解密密码。
/**
 * @author Mydeathtrial
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
```

#### 定制账号退出钩子函数
```
帐号退出钩子函数需要遵循接口cloud.agileframework.security.provider.LogoutProcessorProvider
组件会在产生帐号登出时触发该函数，应用场景很多，如切断指定帐号的websocket。
/**
 * @author Mydeathtrial
 * 描述 帐号退出以后调用
 * @version 1.0
 * @since 1.0
 */
public interface LogoutProcessorProvider {
    /**
     * 退出之后
     *
     * @param username 帐号
     * @param token    身份令牌
     */
    void after(String username, String token);
}
```

#### 定制认证处理结果响应报文
```
定制化结果响应报文，需要遵循接口cloud.agileframework.security.provider.SecurityResultProvider
其中包含三种结果处理能力，其中accessException较为特殊，提供了认证异常的种类，默认处理形式是直接将异常抛出
以此方式将异常交给如@ControllerAdvice等统一异常处理器处理结果视图
/**
 * @author Mydeathtrial
 * 描述 认证结果处理，用于订制响应认证成功/失败/退出等响应信息
 * @version 1.0
 * @since 1.0
 */
public interface SecurityResultProvider {
    /**
     * 认证失败处理
     *
     * @param request  请求
     * @param response 响应
     * @param e        异常
     * @return 响应视图，默认会将异常抛出，供统一异常处理器处理响应
     * @throws Throwable 异常
     */
    default Object accessException(HttpServletRequest request, HttpServletResponse response, Throwable e) throws Throwable {
        throw e;
    }

    /**
     * 登陆成功处理
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 认证成功后的权限数据，其中包含账号信息、令牌信息、权限集合信息
     * @return 响应视图
     */
    default Object loginSuccess(HttpServletRequest request, HttpServletResponse response, UsernamePasswordAuthenticationToken authentication) {
        return authentication;
    }

    /**
     * 退出成功
     *
     * @param request  请求
     * @param response 响应
     * @param username 帐号
     * @param token    令牌
     * @return 响应视图
     */
    default Object logoutSuccess(HttpServletRequest request, HttpServletResponse response, String username, String token) {
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
        modelAndView.addObject("username", username);
        modelAndView.addObject("token", token);
        return modelAndView;
    }
}
```