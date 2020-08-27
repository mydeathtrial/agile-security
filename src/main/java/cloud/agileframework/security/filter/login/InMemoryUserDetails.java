package cloud.agileframework.security.filter.login;

import cloud.agileframework.security.properties.LoginStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author 佟盟
 * 日期 2020/8/00027 17:53
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
public class InMemoryUserDetails implements CustomerUserDetails {
    private LoginStrategy loginStrategy;
    private Collection<? extends GrantedAuthority> authorities;
    private String password;
    private String username;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public static InMemoryUserDetails of(UserDetails userDetails) {
        if(userDetails instanceof InMemoryUserDetails){
            return (InMemoryUserDetails)userDetails;
        }
        InMemoryUserDetails inMemoryUserDetails = new InMemoryUserDetails();
        inMemoryUserDetails.authorities = userDetails.getAuthorities();
        inMemoryUserDetails.password = userDetails.getPassword();
        inMemoryUserDetails.username = userDetails.getUsername();
        inMemoryUserDetails.accountNonExpired = userDetails.isAccountNonExpired();
        inMemoryUserDetails.accountNonLocked = userDetails.isAccountNonLocked();
        inMemoryUserDetails.credentialsNonExpired = userDetails.isCredentialsNonExpired();
        inMemoryUserDetails.enabled = userDetails.isEnabled();
        return inMemoryUserDetails;
    }

    public void setLoginStrategy(LoginStrategy loginStrategy) {
        this.loginStrategy = loginStrategy;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public LoginStrategy getLoginStrategy() {
        return loginStrategy;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
