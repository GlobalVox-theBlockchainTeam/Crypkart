/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.controller.messages.handler;


import com.gradle.components.encrypter.PathVariableEncrypt;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.msg.ChatHistory;
import com.gradle.entity.msg.ChatMessage;
import com.gradle.entity.msg.OutputMessage;
import com.gradle.entity.user.User;
import com.gradle.services.iface.bitcoin.TradeService;
import com.gradle.services.iface.chat.ChatService;
import com.gradle.services.iface.user.UserService;
import com.gradle.util.ActiveSessionManager;
import com.gradle.util.ServiceUtil;
import com.gradle.util.websocket.WebsocketHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDateTime;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class ChatController extends BaseSecurityController implements ActiveSessionManager.ActiveUserChangeListener {

    /*@MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage send(@RequestBody final OutputMessage message) throws Exception {


        final String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time);
    }*/


    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private ActiveSessionManager activeSessionManager;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ServiceUtil serviceUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private WebsocketHelper websocketHelper;

    @Autowired
    private PathVariableEncrypt pathVariableEncrypt;

    @PostConstruct
    private void init() {
        activeSessionManager.registerListener(this);
    }

    @PreDestroy
    private void destroy() {
        activeSessionManager.removeListener(this);
    }

    /**
     * @param modelMap
     * @return
     */
    @RequestMapping("/chatbot")
    public String getChatBot(ModelMap modelMap) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            modelMap.addAttribute("username", getCurrentUserName());
            modelMap.addAttribute("onlineUsers", activeSessionManager.getAllExceptCurrentUser(getCurrentUserName()));
            return "sockJsEndToEndChat";
        }
        return "login";
    }


    /**
     * @param message     parent message which contains old messages
     * @param chatMessage new message which we are going to add to old message object
     * @throws Exception
     */
    @MessageMapping("/chat")
    public void send(@RequestBody Message<ChatHistory> message, @Payload ChatHistory chatMessage) throws Exception {
        boolean validMessage = Jsoup.isValid(chatMessage.getText(), Whitelist.none());
        chatMessage.setText(Jsoup.clean(chatMessage.getText(), Whitelist.none()));
        Integer tradeId = Integer.parseInt(pathVariableEncrypt.decrypt(chatMessage.getTradeId()));
        if (!chatMessage.getText().trim().equals("")) {

            Principal principal = message.getHeaders().get(SimpMessageHeaderAccessor.USER_HEADER, Principal.class);
            if (principal == null) {
                return;
            }
            User userTo = serviceUtil.getUserFromUsernameOrEmail(chatMessage.getRecipient());
            User userFrom = serviceUtil.getUserFromUsernameOrEmail(principal.getName());
            userFrom.setLastSeenAt(new LocalDateTime());
            Trade trade = tradeService.find(tradeId);
            ObjectMapper objectMapper = new ObjectMapper();
            String authenticatedSender = principal.getName();
            /*String time = CommonUtils.getCurrentTimeStamp();
            List<String> users = getAllActiveUsers();*/
            LocalDateTime msgTime = new LocalDateTime();
            String append = (chatMessage.getRecipient().equals(principal.getName())) ? "<br/><span class='text text-danger'>" +
                    "You sent this message to your self. " +
                    "this will not be recorded</span>" : "";
            if (!authenticatedSender.equals(chatMessage.getRecipient())) {
                webSocket.convertAndSendToUser(authenticatedSender, "/queue/messages",
                        new OutputMessage(chatMessage.getFrom(), chatMessage.getText(), msgTime.toString(), true, chatMessage.getTradeId()));

                ChatHistory oldMessage = serviceUtil.getChatHistoryForTrade(userFrom.getId(), userTo.getId(), tradeId);
                ChatMessage chat = new ChatMessage();
                chat.setFrom(userFrom.getUsername());
                chat.setMsg(chatMessage.getText());
                chat.setTime(msgTime);
                if (oldMessage != null) {
                    TypeReference<List<ChatMessage>> mapType = new TypeReference<List<ChatMessage>>() {
                    };
                    List<ChatMessage> jsonChatList = objectMapper.readValue(oldMessage.getFullText(), mapType);
                    jsonChatList.add(chat);
                    String jsonMessage = objectMapper.writeValueAsString(jsonChatList);
                    oldMessage.setFullText(jsonMessage);
                    chatService.saveOrUpdate(oldMessage);
                } else {

                    chatMessage.setUserFrom(userFrom);
                    chatMessage.setUserTo(userTo);
                    chatMessage.setMsgTime(msgTime);
                    chatMessage.setTrade(trade);
                    List<ChatMessage> chatList = new ArrayList<ChatMessage>();
                    chatList.add(chat);
                    String jsonMessage = objectMapper.writeValueAsString(chatList);
                    chatMessage.setFullText(jsonMessage);
                    chatService.saveOrUpdate(chatMessage);
                }


            }



            /*webSocket.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages",
                    new OutputMessage(chatMessage.getFrom(), chatMessage.getText() + append, msgTime.toString(), false, chatMessage.getTradeId()));*/
            websocketHelper.sendChatMessage(userTo, new OutputMessage(chatMessage.getFrom(), chatMessage.getText() + append, msgTime.toString(), false, chatMessage.getTradeId()));
            ;
            OutputMessage outputMessage = new OutputMessage(chatMessage.getFrom(), chatMessage.getFrom() + ": " + chatMessage.getText() + append, msgTime.toString(), false);
            outputMessage.setUrl(serviceUtil.getBaseUrl() + "/trade/process/" + pathVariableEncrypt.encrypt(Integer.toString(trade.getId())));
            websocketHelper.sendWebNotification(userTo, outputMessage);
            userService.saveOrUpdate(userFrom);
            /*webSocket.convertAndSendToUser(chatMessage.getRecipient(), "/queue/notification",
                    outputMessage);*/
            //User userFrom = serviceUtil.getCurrentUser();
        } else if (!validMessage) {
            Principal principal = message.getHeaders().get(SimpMessageHeaderAccessor.USER_HEADER, Principal.class);
            if (principal == null) {
                return;
            }
            String authenticatedSender = principal.getName();
            LocalDateTime msgTime = new LocalDateTime();
            if (!authenticatedSender.equals(chatMessage.getRecipient())) {
                webSocket.convertAndSendToUser(authenticatedSender, "/queue/messages",
                        new OutputMessage(chatMessage.getFrom(), "<span class='alert-danger'>Invalid html found. this message will not be delivered</span>", msgTime.toString(), true, chatMessage.getTradeId()));
            }

        }

    }


    public void sendNotification(User user) throws Exception {
        OutputMessage outputMessage = new OutputMessage(user.getUsername(), "test", "test", false);
        webSocket.convertAndSendToUser(user.getUsername(), "/queue/notification", outputMessage);
    }


    @MessageMapping("/notification")
    public void userActivityMonitor(@RequestBody Message<ChatHistory> message, @Payload ChatHistory chatMessage, Principal principal) {
        User user = serviceUtil.getUserFromUsernameOrEmail(principal.getName());
        user.setLastSeenAt(new LocalDateTime());
        userService.saveOrUpdate(user);
    }

    /**
     * This method will get called when Observable's internal state
     * is changed.
     */
    public void notifyActiveUserChange() {
        Set<String> activeUsers = activeSessionManager.getAll();
        webSocket.convertAndSend("/topic/active", activeUsers);
    }


}
