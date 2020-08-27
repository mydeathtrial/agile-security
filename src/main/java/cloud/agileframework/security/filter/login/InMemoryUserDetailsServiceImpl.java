package cloud.agileframework.security.filter.login;

import cloud.agileframework.spring.util.ServletUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 佟盟
 * 日期 2020/8/00027 15:47
 * 描述 内存形式操作
 * @version 1.0
 * @since 1.0
 */
public class InMemoryUserDetailsServiceImpl implements CustomerUserDetailsService {
    @Autowired
    private AuthenticationManager authenticationManager;
    private final Map<String, InMemoryUserDetails> cache = Maps.newHashMap();

    @Override
    public void validate(UserDetails securityUser) throws AuthenticationException {
        if (!securityUser.isAccountNonExpired()) {
            throw new AccountExpiredException(securityUser.getUsername());
        } else if (!securityUser.isAccountNonLocked()) {
            throw new LockedException(securityUser.getUsername());
        } else if (!securityUser.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(securityUser.getUsername());
        } else if (!securityUser.isEnabled()) {
            throw new BadCredentialsException(securityUser.getUsername());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = ServletUtil.getCurrentRequest();
        final String springSecurityCurrentUser = "SPRING_SECURITY_CURRENT_USER";
        UserDetails user = (UserDetails) request.getAttribute(springSecurityCurrentUser);
        if (user == null) {
            user = cache.get(username);
        }
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        request.setAttribute(springSecurityCurrentUser, user);
        return user;
    }

    @Override
    public void createUser(UserDetails user) {
        updateUser(user);
    }

    @Override
    public void updateUser(UserDetails user) {
        cache.put(user.getUsername(), InMemoryUserDetails.of(user));
    }

    @Override
    public void deleteUser(String username) {
        cache.remove(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext()
                .getAuthentication();

        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context "
                            + "for current user.");
        }

        String username = currentUser.getName();

        // 验证权限
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));

        InMemoryUserDetails user = cache.get(username);

        if (user == null) {
            throw new IllegalStateException("Current user doesn't exist in database.");
        }
        user.setPassword(newPassword);

    }

    @Override
    public boolean userExists(String username) {
        return cache.get(username) != null;
    }
}
