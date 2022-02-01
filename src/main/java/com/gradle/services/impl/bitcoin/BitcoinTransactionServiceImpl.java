/*
 * Copyright (c) 8/3/18 10:46 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.services.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.BitcoinTransactionDao;
import com.gradle.dao.iface.GenericDao;
import com.gradle.entity.bitcoin.BitcoinTransaction;
import com.gradle.services.iface.bitcoin.BitcoinTransactionService;
import com.gradle.services.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service("bitcoinTransactionService")
@Transactional
public class BitcoinTransactionServiceImpl extends GenericServiceImpl<BitcoinTransaction, Integer> implements BitcoinTransactionService {


    @Autowired
    private BitcoinTransactionDao bitcoinTransactionDao;
    public BitcoinTransactionServiceImpl() {

    }
    @Autowired
    public BitcoinTransactionServiceImpl(
            @Qualifier("bitcoinTransactionDao")
                    GenericDao<BitcoinTransaction, Integer> genericDao) {
        super(genericDao);
        this.bitcoinTransactionDao = (BitcoinTransactionDao) genericDao;
    }
}