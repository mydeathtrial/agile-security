package com.agile;

import cloud.agileframework.security.filter.login.CustomerUserDetailsService;
import cloud.agileframework.security.filter.login.InMemoryUserDetails;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 佟盟
 * 日期 2020/8/00027 19:03
 * 描述 TODO
 * @version 1.0
 * @since 1.0
 */
@Component
public class Test implements InitializingBean {
    @Autowired(required = false)
    CustomerUserDetailsService customerUserDetailsService;


    @Override
    public void afterPropertiesSet() throws Exception {
        if (customerUserDetailsService == null) {
            return;
        }
        InMemoryUserDetails user = new InMemoryUserDetails();
        user.setPassword("$2a$04$VABavUN0MV17fvBVYwUV9./TfINWBFrbS8aSnbD15n7kgZ7MwpEpW");
        user.setUsername("admin");
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        customerUserDetailsService.createUser(user);
    }
}
