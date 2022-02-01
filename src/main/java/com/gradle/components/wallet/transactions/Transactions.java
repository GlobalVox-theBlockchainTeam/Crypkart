/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.components.wallet.transactions;

import com.gradle.entity.forms.BitcoinDataList;
import com.gradle.util.ServiceUtil;
import org.apache.log4j.Logger;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;




@Component
public class Transactions {
    public static final Logger logger = Logger.getLogger(Transactions.class);

    @Autowired
    private ServiceUtil serviceUtil;


    /**
     * @param max : maximum transaction to fetch (0 for all)
     * @param netParams : Network parameters to connect to blockchain node
     * @param wallet : Wallet object(each user has different wallet file in files folder
    * */
    public List<BitcoinDataList> getTransactionFromWallet(Wallet wallet, NetworkParameters netParams, int max) {

        int transactionCount = 0;
        // get wallet transactions
        Set<Transaction> transactionSet = wallet.getTransactions(false);
        List<BitcoinDataList> bitcoinDataLists = new ArrayList<BitcoinDataList>();

        

        // Iterate through each transactions
        for (Transaction tx : transactionSet) {
            if (transactionCount >= max && max != 0)
                break;



            // store values
            List<String> valList = new ArrayList<String>();
            // store receivers for each transaction
            List<String> rList = new ArrayList<String>();
            // store sender for each transactions
            List<String> sList = new ArrayList<String>();
            // store whole transactions details
            BitcoinDataList bdObj = new BitcoinDataList();

            // setting transaction hash code
            bdObj.setTransactionId(tx.getHashAsString());
            // setting transaction type(if received or sent)
            // if total sendToMe is greater that means you received some bitcoins else you sent some bitcoins
            /*bdObj.setType(((tx.getValueSentToMe(wallet).longValue() > tx.getValueSentFromMe(wallet).longValue())) ? "R" : " ");
            boolean sent = tx.getValue(wallet).signum() < 0;*/
            bdObj.setType((tx.getValue(wallet).signum() < 0) ? " " : "R" );





            // iterate through each output for specific transaction
            for (TransactionOutput output : tx.getOutputs()) {
                try {
                    // output.ismine method will check if particular output address was generated for your wallet
                    /*if (output.isMine(wallet) || !output.isMine(wallet)) {*/

                    // get transaction outpur script which includes plain data
                    Script script = output.getScriptPubKey();
                    Address address = script.getToAddress(netParams, true);
                    rList.add(address.toString());
                    valList.add(output.getValue().toFriendlyString());
                    /*}*/
                } catch (ScriptException se) {
                    logger.error(se.getMessage());
                }
            }

            // iterate through each output for specific transaction
            for (TransactionInput in : tx.getInputs()) {
                try {
                    // check if it is coinbase block
                    Address a = in.getFromAddress();
                    in.isCoinBase();
                    Script script = in.getScriptSig();
                    Address address = script.getFromAddress(netParams);
                    sList.add(address.toString());
                } catch ( Exception x) {
                    logger.error(x.getMessage());
                    System.out.println(x.getMessage());
                }
            }
            // set all data to our list
            bdObj.setReceiver(rList);
            bdObj.setValue(valList);
            bdObj.setSender(sList);
            bitcoinDataLists.add(bdObj);
            transactionCount++;
        }

        return bitcoinDataLists;
    }


    /**
     *
     * @param wallet    : wallet object
     * @param netParams : Network parameters to connect to node
     * @param max       : Maximum transaction to be fetch(0 for all)
     * @return          : Transaction contain by wallet object
     */
    public List<BitcoinDataList> getPendingTransactionFromWallet(Wallet wallet, NetworkParameters netParams, int max) {

        int transactionCount = 0;
        // get wallet transactions
        Collection<Transaction> transactionSet = wallet.getPendingTransactions();
        List<BitcoinDataList> bitcoinDataLists = new ArrayList<BitcoinDataList>();

        // Iterate through each transactions
        for (Transaction tx : transactionSet) {
            if (transactionCount >= max && max != 0)
                break;

            // store values
            List<String> valList = new ArrayList<String>();
            // store receivers for each transaction
            List<String> rList = new ArrayList<String>();
            // store sender for each transactions
            List<String> sList = new ArrayList<String>();
            // store whole transactions details
            BitcoinDataList bdObj = new BitcoinDataList();

            // setting transaction hash code
            bdObj.setTransactionId(tx.getHashAsString());
            // setting transaction type(if received or sent)
            // if total sendToMe is greater that means you received some bitcoins else you sent some bitcoins
            bdObj.setType(((tx.getValueSentToMe(wallet).longValue() > tx.getValueSentFromMe(wallet).longValue())) ? "R" : " ");

            // iterate through each output for specific transaction
            for (TransactionOutput output : tx.getOutputs()) {
                try {
                    // output.ismine method will check if particular output address was generated for your wallet
                    /*if (output.isMine(wallet) || !output.isMine(wallet)) {*/

                    // get transaction outpur script which includes plain data
                    Script script = output.getScriptPubKey();
                    Address address = script.getToAddress(netParams, true);
                    rList.add(address.toString());
                    valList.add(output.getValue().toFriendlyString());
                    /*}*/
                } catch (ScriptException se) {
                    logger.error(se.getMessage());
                }
            }

            // iterate through each output for specific transaction
            for (TransactionInput in : tx.getInputs()) {
                try {
                    // check if it is coinbase block
                    in.isCoinBase();
                    Script script = in.getScriptSig();
                    Address address = script.getFromAddress(netParams);
                    sList.add(address.toString());
                } catch (final ScriptException x) {
                    try {

                    } catch (Exception e2) {
                    }
                }
            }
            // set all data to our list
            bdObj.setReceiver(rList);
            bdObj.setValue(valList);
            bdObj.setSender(sList);
            bitcoinDataLists.add(bdObj);
            transactionCount++;
        }

        return bitcoinDataLists;
    }

    /**
     *
     * @param netParams : Network parameters to connect to blockchain node
     * @param wallet : Wallet object(each user has different wallet file in files folder
     * @return pending transactions value in bitcoinj coin
     */

    public Coin getPendingTransactionValue(Wallet wallet, NetworkParameters netParams) {
        Coin value = Coin.ZERO;
        // get all transaction outputs to calculate pending spendings

        List<TransactionOutput> candidates = wallet.calculateAllSpendCandidates(false, false);
        for (TransactionOutput out : candidates) {
            if (out.getParentTransaction().getAppearsInHashes() == null) {
                // Not seen in any block that we know of
                value = value.add(out.getValue());
            }
        }

        return value;
    }
}
