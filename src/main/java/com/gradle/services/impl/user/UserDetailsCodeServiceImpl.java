/*
 * Copyright (c) 20/4/18 3:44 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.entity.user.Role;
import com.gradle.entity.user.User;
import com.gradle.services.iface.user.RoleService;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.constants.ConstantProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service("userDetailsCodeService")
public class UserDetailsCodeServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Object[] params = new Object[1];
        params[0] = username;
        User user = userService.first(" from User where username=?", params);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if (user.getGoogleAuthenticatorKey() != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority(ConstantProperties.ROLE_GUEST));
        } else {
            for (Role role : user.getRoles()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));
            }
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
