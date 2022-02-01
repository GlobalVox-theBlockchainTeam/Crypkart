/*
 * Copyright (c) 8/3/18 10:46 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.CurrencyDao;
import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.Currency;
import com.gradle.services.iface.bitcoin.CurrencyService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("currencyService")
@Transactional
public class CurrencyServiceImpl extends GenericServiceImpl<Currency, Integer> implements CurrencyService {

    @Autowired
    private CurrencyDao currencyDao;



    public CurrencyServiceImpl() {

    }

    @Autowired
    public CurrencyServiceImpl(@Qualifier("currencyDao") GenericDao<Currency, Integer> genericDao) {
        super(genericDao);
        this.currencyDao = (CurrencyDao) genericDao;
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
