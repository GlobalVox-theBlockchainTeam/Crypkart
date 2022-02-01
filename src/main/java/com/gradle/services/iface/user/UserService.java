/*
 * Copyright (c) 8/3/18 10:44 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.iface.user;

import com.gradle.entity.user.User;
import com.gradle.services.iface.GenericService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService extends GenericService<User,Integer> {

    public void createVerificationToken(User user, String token);
    public List<User> getChildUsers(User user);
    /*public org.springframework.security.core.userdetails.User loadUserByUsername(String username) throws UsernameNotFoundException;*/
}
