/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.msg;

import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

/**
 * ChatMessage dto class to be used in MVC pattern
 *
 * @author Yasitha Thilakaratne
 */
@Entity
@Table(name = "chat_history")
public class ChatHistory extends BaseModel{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "sender")
    @HtmlValidateConstraint(whiteListType = "none")
    private String from;

    @HtmlValidateConstraint(whiteListType = "none")
    @Column(name = "message")
    private String text;

    @Column(name = "full_message", columnDefinition = "text")
    private String fullText;

    @Column(name = "recipient")
    @HtmlValidateConstraint(whiteListType = "none")
    private String recipient;

    @Transient
    private String tradeId;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_user_id", nullable = false, referencedColumnName = "id")
    private User userFrom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_user_id", nullable = false, referencedColumnName = "id")
    private User userTo;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trade_id", nullable = false, referencedColumnName = "id")
    private Trade trade;

    @DateTimeFormat(pattern = "dd/MM/yyyy h:i:s")
    @Column(name = "msg_time")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime msgTime;

    public ChatHistory(String from, String text, String recipient, String tradeId) {
        this.from = from;
        this.text = text;
        this.recipient = recipient;
        this.tradeId = tradeId;

    }

    public ChatHistory() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public LocalDateTime getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(LocalDateTime msgTime) {
        this.msgTime = msgTime;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }
}
