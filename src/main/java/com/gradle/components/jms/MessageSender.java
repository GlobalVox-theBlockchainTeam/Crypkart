/*
 * Copyright (c) 4/4/18 3:48 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.components.jms;

import com.gradle.entity.user.UserWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;


@Component
public class MessageSender {

    public static final String QUEUE_NAME = "wallet";

    public static final String SEND_COIN_QUEUE = "wallet_send_coin";

    @Autowired
    JmsTemplate jmsTemplate;

    /**
     *
     * @param userWallet
     *
     * This method will put a message in wallet queue
     * in response will get a new wallet address
     */
    public void sendMessage(final UserWallet userWallet) {
        //jmsTemplate.convertAndSend("oqueue", str);
        jmsTemplate.convertAndSend(QUEUE_NAME, userWallet);
    }


    /**
     *
     * @param userWallet
     * This will put a new send coin transaction in queue and ohter application will process that transaction on blockchain
     * 
     *
     */
    public void sendCoinMessage(final UserWallet userWallet) {
        jmsTemplate.convertAndSend(SEND_COIN_QUEUE, userWallet);
    }

    /*public void sendMessage(final UserWallet userWallet) {
        //jmsTemplate.convertAndSend("oqueue", str);
        userWallet.setId(45);

        jmsTemplate.convertAndSend(QUEUE_NAME, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage(userWallet);
                return objectMessage;
            }
        });
    }*/
}