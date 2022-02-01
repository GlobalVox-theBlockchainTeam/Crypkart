/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.bitcoin;

import com.gradle.entity.Currency;
import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

/**
 * BitcoinTransaction Entity
 * For more details check (bitcoin_transactions table in database)
 */

@Entity
@Table(name = "bitcoin_transactions")
public class BitcoinTransaction extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id", nullable = false, referencedColumnName = "id")
    private Currency currency;

    @NotEmpty
    @Column(name = "transaction_id", nullable = false)
    @HtmlValidateConstraint(whiteListType = "none")
    private String transactionId;

    @NotEmpty
    @Column(name = "from__wallet_id")
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style","div:style"})
    private String fromWalletId;

    @NotEmpty
    @Column(name = "to_wallet_id")
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style","div:style"})
    private String toWalletId;

    @NotEmpty
    @Column(name = "btc_amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String btcAmount;

    @NotEmpty
    @Column(name = "btc_fees_amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String btcFeesAmount;

    @Column(name = "currency_amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String currencyAmount;

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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFromWalletId() {
        return fromWalletId;
    }

    public void setFromWalletId(String fromWalletId) {
        this.fromWalletId = fromWalletId;
    }

    public String getToWalletId() {
        return toWalletId;
    }

    public void setToWalletId(String toWalletId) {
        this.toWalletId = toWalletId;
    }

    public String getBtcAmount() {
        return btcAmount;
    }

    public void setBtcAmount(String btcAmount) {
        this.btcAmount = btcAmount;
    }

    public String getBtcFeesAmount() {
        return btcFeesAmount;
    }

    public void setBtcFeesAmount(String btcFeesAmount) {
        this.btcFeesAmount = btcFeesAmount;
    }

    public String getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(String currencyAmount) {
        this.currencyAmount = currencyAmount;
    }


    @Override
    @PrePersist
    public void preInsert() {
        this.createdAt = new LocalDateTime();
        if (this.btcFeesAmount == null || this.btcFeesAmount.isEmpty()) {
            this.btcFeesAmount = "0.0";
        }
    }

//833997826

}
