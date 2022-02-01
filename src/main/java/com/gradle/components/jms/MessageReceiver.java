/*
 * Copyright (c) 4/4/18 3:46 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.components.jms;

import com.gradle.entity.bitcoin.DirectTransaction;
import com.gradle.entity.user.User;
import com.gradle.entity.user.UserWallet;
import com.gradle.services.iface.bitcoin.DirectTransactionService;
import com.gradle.services.iface.user.UserService;
import com.gradle.services.iface.user.UserWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.time.LocalDateTime;


@Component
public class MessageReceiver {
    static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

    private static final String WALLET_RESPONSE_QUEUE = "wallet_response";
    private static final String COIN_RECEIVED = "coin_received";
    private static final String COIN_SENT = "wallet_sent_coin";

    @Autowired
    MessageSender messageSender;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private DirectTransactionService directTransactionService;

    @Autowired
    UserService userService;
    /*@JmsListener(destination = ORDER_RESPONSE_QUEUE)
    public void receiveMessage(Message<Countries> message) throws JMSException {
        LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        MessageHeaders headers =  message.getHeaders();
        LOG.info("Application : headers received : {}", headers);
        Countries response = message.getPayload();
        LOG.info("Application : response received : {}",response);
        LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }*/


    /**
     *
     * @param message       : will read message from Queue - wallet_response
     * @throws JMSException
     */
    @JmsListener(destination = WALLET_RESPONSE_QUEUE)
    public void receiveMessageString(Message<UserWallet> message) throws JMSException {
        try {
            LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            MessageHeaders headers = message.getHeaders();
            UserWallet userWallet = message.getPayload();
            User user = userService.find(userWallet.getUid());
            userWallet.setUser(user);
            String query = "update UserWallet set current_address=0 where user_id=?";
            Object[] params = new Object[1];
            params[0] = user.getId();
            if (!userWalletService.executeQuery(query, params)) {
                LOG.error("user wallets were not updated for user id " + user.getId() + " time :  " + LocalDateTime.now());
            }
            LOG.info("Application : headers received : {}", headers);
            System.out.println(userWallet.getWalletAddress());
            userWalletService.save(userWallet);
            // String convertedMessage = ((TextMessage) message).getText();
            LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        } catch (Exception e) {
            LOG.error(e.getMessage() + e.getStackTrace());
        }
    }


    /**
     *
     * @param message       Will read message from queue - coin_received
     * @throws JMSException
     *
     * Will be invoked when there is any coin received event will take place on block chain on our wallet
     * This listener will process message from queue coin_received
     * - first will find transaction details from transaction id of block chain
     * - will put a new message in queue to get a new wallet address for user
     */
    @JmsListener(destination = COIN_RECEIVED)
    public void coinReceived(Message<UserWallet> message) throws JMSException {

        try {
            LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
            MessageHeaders headers = message.getHeaders();
            UserWallet userWallet = message.getPayload();
            String address = userWallet.getWalletAddress();
            String query = "from UserWallet where allocated_wallet_address=? and current_address=?";
            Object[] params = new Object[2];
            params[0] = address;
            params[1] = 1;
            UserWallet currentWallet = userWalletService.first(query, params);
            DirectTransaction directTransaction = new DirectTransaction();
            if (userWallet.getTransactionId() != null) {
                String dQuery = "from DirectTransaction where transaction_id=?";
                Object[] dParams = new Object[1];
                dParams[0] = userWallet.getTransactionId();
                DirectTransaction directTransactionDb = directTransactionService.first(dQuery, dParams);
                if (directTransactionDb != null) {
                    directTransactionDb.setTotalConfirmation(userWallet.getTotalConfirmation());
                    directTransactionService.saveOrUpdate(directTransactionDb);
                    String updateQuery = "update DirectTransaction set confirmation_count=? where transaction_id=?";
                    Object[] updateParams = new Object[2];
                    updateParams[1] = userWallet.getTransactionId();
                    updateParams[0] = userWallet.getTotalConfirmation();
                    if (!directTransactionService.executeQuery(updateQuery, updateParams)) {
                        LOG.error("user wallets were not updated for transaction id " + userWallet.getTransactionId() + " time :  " + LocalDateTime.now());
                    }
                } else {
                    directTransaction.setUser(currentWallet.getUser());

                    directTransaction.setBitcoinAmount(userWallet.getAmount());
                    directTransaction.setIncomingWalletAddress(address);
                    directTransaction.setEmail(currentWallet.getUser().getEmail());
                    if (userWallet.isOutgoing()) {
                        directTransaction.setOutgoing(true);
                        directTransaction.setOutgoingWalletAddress(userWallet.getWalletAddress());
                        directTransaction.setIncomingWalletAddress("");
                    } else {
                        directTransaction.setOutgoing(false);
                        directTransaction.setOutgoingWalletAddress("");
                        directTransaction.setIncomingWalletAddress(userWallet.getWalletAddress());
                    }
                    directTransaction.setTransactionId(userWallet.getTransactionId());
                    directTransaction.setTotalConfirmation(userWallet.getTotalConfirmation());
                    directTransactionService.save(directTransaction);
                    UserWallet newWallet = new UserWallet();
                    userWallet.setUid(currentWallet.getUser().getId());
                    messageSender.sendMessage(userWallet);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage() + e.getStackTrace());
        }

    }

    /**
     *
     * @param message       Will read message from queue - coin_received
     * @throws JMSException
     *
     * Will be invoked when there is any coin sent event will take place on block chain on our wallet
     * This listener will process message from queue wallet_sent_coin
     * - will send coin sent details to mq and our other application will process that transaction on blockchain
     * - will put a new message in queue to get a new wallet address for user
     */
    @JmsListener(destination = COIN_SENT)
    public void coinSent(Message<UserWallet> message) throws JMSException {
        try {
            MessageHeaders headers = message.getHeaders();
            UserWallet userWallet = message.getPayload();
            String address = userWallet.getWalletAddress();
            String query = "from UserWallet where allocated_wallet_address=? and current_address=?";
            Object[] params = new Object[2];
            params[0] = address;
            params[1] = 1;
            UserWallet currentWallet = userWalletService.first(query, params);
            DirectTransaction directTransaction = new DirectTransaction();
            if (userWallet.getCurrentTransactionId() != 0) {
                String dQuery = "from DirectTransaction where id=?";
                Object[] dParams = new Object[1];
                dParams[0] = userWallet.getCurrentTransactionId();
                directTransaction = directTransactionService.first(dQuery, dParams);
                if (directTransaction != null) {
                    directTransaction.setTotalConfirmation(userWallet.getTotalConfirmation());
                    directTransaction.setBitcoinAmount(userWallet.getAmount());
                    directTransaction.setTransactionId(userWallet.getTransactionId());
                    directTransaction.setTotalConfirmation(userWallet.getTotalConfirmation());
                    directTransactionService.saveOrUpdate(directTransaction);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage() + e.getStackTrace());
        }
    }
}

