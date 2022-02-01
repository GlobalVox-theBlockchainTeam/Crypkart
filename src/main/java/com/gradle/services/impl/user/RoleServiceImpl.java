/*
 * Copyright (c) 8/3/18 10:47 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.user;

import com.gradle.dao.iface.GenericDao;
import com.gradle.dao.iface.user.RoleDao;
import com.gradle.entity.user.Role;
import com.gradle.services.iface.user.RoleService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("roleService")
@Transactional
public class RoleServiceImpl extends GenericServiceImpl<Role, Integer> implements RoleService {



    @Autowired
    private RoleDao roleDao;

    public RoleServiceImpl() {

    }

    @Autowired
    public RoleServiceImpl(
            @Qualifier("roleDao")
                    GenericDao<Role, Integer> genericDao) {
        super(genericDao);
        this.roleDao = (RoleDao) genericDao;
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
