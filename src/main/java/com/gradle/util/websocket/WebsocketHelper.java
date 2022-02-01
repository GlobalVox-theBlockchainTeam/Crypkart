/*
 * Copyright (c) 9/4/18 2:38 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util.websocket;

import com.gradle.entity.msg.OutputMessage;
import com.gradle.entity.user.User;
import org.apache.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Websocket helper
 * Send notifcation and send chat message from any controller
 */
public class WebsocketHelper {

    public static final Logger logger = Logger.getLogger(WebsocketHelper.class);

    @Autowired
    protected SimpMessagingTemplate webSocket;

    public void sendWebNotification(User receiver,OutputMessage outputMessage){
        try {
            if (receiver.isEnableWebNotification()) {
                webSocket.convertAndSendToUser(receiver.getUsername(), "/queue/notification", outputMessage);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    public void sendChatMessage(User receiver, OutputMessage outputMessage){
        try{
            webSocket.convertAndSendToUser(receiver.getUsername(), "/queue/messages", outputMessage);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
