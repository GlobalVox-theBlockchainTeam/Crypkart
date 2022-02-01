/*
 * Copyright (c) 8/3/18 10:47 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.UserDao;

import com.gradle.entity.user.User;
import com.gradle.services.iface.user.UserService;
import com.gradle.services.iface.user.VerificationService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userService")
@Transactional
public class UserServiceImpl extends GenericServiceImpl<User, Integer> implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private VerificationService verificationService;


    public UserServiceImpl() {

    }

    @Autowired
    public UserServiceImpl(
            @Qualifier("userDao")
                    GenericDao<User, Integer> genericDao) {
        super(genericDao);
        this.userDao = (UserDao) genericDao;
    }

    @Override
    public void createVerificationToken(User user, String token) {
        this.userDao.createVerificationToken(user, token);
    }

    @Override
    public List<User> getChildUsers(User user) {
        return this.userDao.getChildUsers(user);
    }

/*@Override
    @Transactional
    public org.springframework.security.core.userdetails.User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.find(1);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (UserRole role : userRoleDao.findAll()){
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }*/
}
