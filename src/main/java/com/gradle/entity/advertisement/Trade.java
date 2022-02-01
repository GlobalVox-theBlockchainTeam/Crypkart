/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.advertisement;

import com.gradle.entity.base.BaseModel;
import com.gradle.entity.bitcoin.Escrow;
import com.gradle.entity.msg.ChatFiles;
import com.gradle.entity.msg.ChatHistory;
import com.gradle.entity.user.FeedBack;
import com.gradle.entity.user.User;
import com.gradle.validator.iface.HtmlValidateConstraint;
import com.gradle.validator.iface.TradeConstraint;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "trade_master")
@TradeConstraint

public class Trade extends BaseModel implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;



    @Column(name = "trade_id", nullable = false)
    private String tradeId;



    @Column(name = "trade_sequence_id", nullable = false)
    private String tradeSequenceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "advertiser_id", nullable = false, referencedColumnName = "id")
    private User trader;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id", nullable = false, referencedColumnName = "id")
    private User buyer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false, referencedColumnName = "id")
    private User seller;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trade_status_id", nullable = false, referencedColumnName = "id")
    private TradeStatus tradeStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "advertise_id", nullable = false, referencedColumnName = "id")
    private Advertise advertise;


    @CsvBindByName(column = "username")
    @Transient
    private String username;

    @CsvBindByName(column = "tradername")
    @Transient
    private String tradername;

    @CsvBindByName(column = "advertisetitle")
    @Transient
    private String advertisetitle;
    @CsvBindByName(column = "tradestatus")
    @Transient
    private String tradestatus;





    @Column(name = "payment_hash")
    @HtmlValidateConstraint(whiteListType = "basic")
    private String paymentHash;

    @Column(name = "btc_price")
    @HtmlValidateConstraint(whiteListType = "none")
    private String btcPrice;

    @NotEmpty
    @Digits(integer = 10, fraction = 8)
    @Column(name = "btc_amount", nullable = false)
    @CsvBindByName(column = "BTC Amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String btcAmount;

    @NotEmpty
    @Digits(integer = 10, fraction = 8)
    @Column(name = "amount", nullable = false)
    @CsvBindByName(column = "Amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String amount;

    @Column(name = "payment_instructions", columnDefinition = "text")
    @HtmlValidateConstraint(whiteListType = "basic", addAttributes = {"span:style"})
    private String paymentInstructions;

    @DateTimeFormat(pattern = "dd/MM/yyyy h:i:s")
    @Column(name = "timeout")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime timeOut;


    @DateTimeFormat(pattern = "dd/MM/yyyy h:i:s")
    @Column(name = "payment_sent_time")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime paymentSentTime;

    @DateTimeFormat(pattern = "dd/MM/yyyy h:i:s")
    @Column(name = "bitcoin_release_time")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime bitcoinReleaseTime;


    @DateTimeFormat(pattern = "dd/MM/yyyy h:i:s")
    @Column(name = "bitcoin_escrow_time")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime bitcoinEscrowTime;


    @Column(name = "bitcoin_release_minutes")
    @HtmlValidateConstraint(whiteListType = "none")
    private String bitcoinReleaseMinutes;

    @CsvBindByName(column = "Released Bitcoins")
    @Column(name = "released_bitcoins")
    @HtmlValidateConstraint(whiteListType = "none")
    private String releasedBitcoins;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "referal_id", nullable = false, referencedColumnName = "id")
    private User refereal;

    @Column(name = "attachment", columnDefinition = "text")
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style","div:style"})
    private String attachment;


    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ChatHistory> chatHistoryList;

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Escrow> escrowSet;

    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ChatFiles> chatFilesList ;


    @OneToMany(mappedBy = "trade", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<FeedBack> toFeedBacks;


    @Column(name = "feedback_trader", nullable = true, columnDefinition = "bit(1) default 0")
    private boolean feedbackFromTrader;

    @Column(name = "feedback_advertiser", nullable = true, columnDefinition = "bit(1) default 0")
    private boolean feedbackFromAdvertiser;








    public boolean isFeedbackFromTrader() {
        return feedbackFromTrader;
    }

    public void setFeedbackFromTrader(boolean feedbackFromTrader) {
        this.feedbackFromTrader = feedbackFromTrader;
    }

    public boolean isFeedbackFromAdvertiser() {
        return feedbackFromAdvertiser;
    }

    public void setFeedbackFromAdvertiser(boolean feedbackFromAdvertiser) {
        this.feedbackFromAdvertiser = feedbackFromAdvertiser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getTrader() {
        return trader;
    }

    public void setTrader(User trader) {
        this.trader = trader;
    }

    public Advertise getAdvertise() {
        return advertise;
    }

    public void setAdvertise(Advertise advertise) {
        this.advertise = advertise;
    }

    public String getPaymentHash() {
        return paymentHash;
    }

    public void setPaymentHash(String paymentHash) {
        this.paymentHash = paymentHash;
    }

    public String getBtcPrice() {
        return btcPrice;
    }

    public void setBtcPrice(String btcPrice) {
        this.btcPrice = btcPrice;
    }

    public String getBtcAmount() {
        return btcAmount;
    }

    public void setBtcAmount(String btcAmount) {
        this.btcAmount = btcAmount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentInstructions() {
        return paymentInstructions;
    }

    public void setPaymentInstructions(String paymentInstructions) {
        this.paymentInstructions = paymentInstructions;
    }

    public LocalDateTime getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(LocalDateTime timeOut) {
        this.timeOut = timeOut;
    }

    public String getReleasedBitcoins() {
        return releasedBitcoins;
    }

    public void setReleasedBitcoins(String releasedBitcoins) {
        this.releasedBitcoins = releasedBitcoins;
    }



    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Set<ChatHistory> getChatHistoryList() {
        return chatHistoryList;
    }

    public void setChatHistoryList(Set<ChatHistory> chatHistoryList) {
        this.chatHistoryList = chatHistoryList;
    }

    public Set<ChatFiles> getChatFilesList() {
        return chatFilesList;
    }

    public void setChatFilesList(Set<ChatFiles> chatFilesList) {
        this.chatFilesList = chatFilesList;
    }

    public User getRefereal() {
        return refereal;
    }

    public void setRefereal(User refereal) {
        this.refereal = refereal;
    }


    public String getUsername() {
        return this.user.getUsername();
    }



    public String getTradername() {
        return this.trader.getUsername();
    }



    public String getAdvertisetitle() {
        return this.advertise.getAdvertisementType().getValue();
    }



    public String getTradestatus() {
        return this.tradeStatus.getStatusLabel();
    }

    public Set<FeedBack> getToFeedBacks() {
        return toFeedBacks;
    }

    public void setToFeedBacks(Set<FeedBack> toFeedBacks) {
        this.toFeedBacks = toFeedBacks;
    }

    public LocalDateTime getBitcoinReleaseTime() {
        return bitcoinReleaseTime;
    }

    public String getBitcoinReleaseMinutes() {
        return bitcoinReleaseMinutes;
    }

    public void setBitcoinReleaseMinutes(String bitcoinReleaseMinutes) {
        this.bitcoinReleaseMinutes = bitcoinReleaseMinutes;
    }

    public void setBitcoinReleaseTime(LocalDateTime bitcoinReleaseTime) {
        this.bitcoinReleaseTime = bitcoinReleaseTime;
    }

    public LocalDateTime getPaymentSentTime() {
        return paymentSentTime;
    }

    public void setPaymentSentTime(LocalDateTime paymentSentTime) {
        this.paymentSentTime = paymentSentTime;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }


    public LocalDateTime getBitcoinEscrowTime() {
        return bitcoinEscrowTime;
    }

    public void setBitcoinEscrowTime(LocalDateTime bitcoinEscrowTime) {
        this.bitcoinEscrowTime = bitcoinEscrowTime;
    }

    public Set<Escrow> getEscrowSet() {
        return escrowSet;
    }

    public void setEscrowSet(Set<Escrow> escrowSet) {
        this.escrowSet = escrowSet;
    }

    public String getTradeSequenceId() {
        return tradeSequenceId;
    }

    public void setTradeSequenceId(String tradeSequenceId) {
        this.tradeSequenceId = tradeSequenceId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    @Override
    public void preInsert() {
        super.preInsert();
        this.feedbackFromAdvertiser=false;
        this.feedbackFromTrader=false;
    }
}
