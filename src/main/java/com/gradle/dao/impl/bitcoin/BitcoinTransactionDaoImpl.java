/*
 * Copyright (c) 8/3/18 10:43 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.dao.impl.bitcoin;

import com.gradle.dao.iface.bitcoin.BitcoinTransactionDao;
import com.gradle.dao.impl.GenericDaoImpl;
import com.gradle.entity.bitcoin.BitcoinTransaction;
import org.springframework.stereotype.Repository;

@Repository("bitcoinTransactionDao")
public class BitcoinTransactionDaoImpl extends GenericDaoImpl<BitcoinTransaction, Integer> implements BitcoinTransactionDao{

}
