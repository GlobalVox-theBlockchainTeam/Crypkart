/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.components.wallet.kit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.gradle.entity.forms.SendCoinForm;
import com.gradle.entity.user.User;
import com.gradle.util.constants.ConstantProperties;
import org.apache.log4j.Logger;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;


/**
 *@class : This class uses Bitcoinj library to connect to bitcoin node and to fetch related values
 */
@Component
public class WKit {
    public static final Logger logger = Logger.getLogger(WKit.class);

    /**
     *
     * @param user      Current user Object
     * @param form      Sendcoin form Object containing sending information(Receiver address, coin to be sent)
     * @param netParams Network parameters which are being used to connect to bitcoin node
     * @throws Exception
     */
    @Async("threadPoolTaskExecutor")
    public void sendCoinsTo(User user, SendCoinForm form, NetworkParameters netParams) throws Exception {

        // Initializing AppKit which will be used to connect to node and put new transaction on that node
//        WalletAppKit kit = new WalletAppKit(netParams, new File(ConstantProperties.USER_WALLET_FILE_PATH), user.getEmail());
        WalletAppKit kit = new WalletAppKit(netParams, new File(ConstantProperties.USER_WALLET_FILE_PATH), user.getId() + "");

        kit.setAutoSave(true);
        kit.setAutoStop(true);


        /**
         * Starting synch with node.(in case you have local node it will try to connect it
         * You can also specify specific node using peerGroup object but not recommended
         */
        kit.startAsync();
        kit.awaitRunning();
        Coin value = Coin.parseCoin(form.getBitcoin().trim());
        Address to = Address.fromBase58(netParams, form.getWalletAddress().trim());
        try {
            Wallet.SendResult sendResult = kit.wallet().sendCoins(kit.peerGroup(), to, value);
            //Will wait here until hear from block chain
            sendResult.broadcastComplete.get();

            /**
             * After successful broadcast we will have following listener
             * onsuccess : if Kit was able to put transaction on chain successfully
             * onfailure : if it failed(with reason)
             */
            Futures.addCallback(sendResult.broadcastComplete, new FutureCallback<Transaction>() {
                @Override
                public void onSuccess(org.bitcoinj.core.Transaction resTx) {
                    logger.info("PayConfig_sendCoin: Something went wrong !!!");
                }

                @Override
                public void onFailure(Throwable t) {
                    logger.info("PayConfig_sendCoin: Something went wrong !!!");
                }
            });

            //Finally stop syncing with chain or else it will continue it and we will not be able to initialize it again
            kit.stopAsync();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }


    /**
     *
     * @param user      : Current user Object
     * @param netParams : Network parameters to connect to node
     */
    @Async("threadPoolTaskExecutor")
    public void synchWallet(User user, NetworkParameters netParams) {
        try {

            /*WalletAppKit kit = new WalletAppKit(netParams, new File(ConstantProperties.USER_WALLET_FILE_PATH), user.getEmail());
            kit.setAutoSave(true);
            kit.setAutoStop(true);

            *//**
             * Starting synch with node.(in case you have local node it will try to connect it
             * You can also specify specific node using peerGroup object but not recommended
             *//*
            kit.startAsync();
            kit.awaitRunning();
            Set<Transaction> transactions = kit.wallet().getTransactions(true);
            kit.store().getChainHead();

            kit.wallet().getTransaction(new Sha256Hash("7215dd3b9139e1d7e0e7077339b41a1310db01b7dc738f884e90268d69adbde7"));
            Set<Transaction> transactionSet = kit.wallet().getTransactions(false);
            for (Transaction tx : transactionSet) {
                try {
                    if (tx.getHashAsString().equalsIgnoreCase("7215dd3b9139e1d7e0e7077339b41a1310db01b7dc738f884e90268d69adbde7")){
                        String a = "A";
                    }

                    for (TransactionInput in : tx.getInputs()) {
                        if (in.getScriptSig().isSentToMultiSig()){
                            String a="A";
                        }
                    }

                }catch(Exception e) {
                    logger.info("Error");
                }


            }
            //kit.wait(3);
            logger.info(kit.store().getChainHead().getHeader());

            //Finally stop syncing with chain or else it will continue it and we will not be able to initialize it again
            kit.stopAsync();*/
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
