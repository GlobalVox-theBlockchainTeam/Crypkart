/*
 * Copyright (c) 6/4/18 2:49 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.bitcoin;

import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import com.gradle.validator.iface.ExternalTransferConstraint;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;


@Entity
@Table(name = "direct_transactions")
public class DirectTransaction extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "incoming_wallet_address")
    @HtmlValidateConstraint(whiteListType = "none")
    private String incomingWalletAddress;


    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "outgoing_wallet_address")
    @HtmlValidateConstraint(whiteListType = "none")
    private String outgoingWalletAddress;

    @Column(name = "outgoing_transaction")
    private boolean outgoing;

    @Column(name = "bitcoin_amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String bitcoinAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "confirmation_count", columnDefinition = "int(11) default 0")
    private int totalConfirmation;

    @Column(name = "remarks")
    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIncomingWalletAddress() {
        return incomingWalletAddress;
    }

    public void setIncomingWalletAddress(String incomingWalletAddress) {
        this.incomingWalletAddress = incomingWalletAddress;
    }

    public String getOutgoingWalletAddress() {
        return outgoingWalletAddress;
    }

    public void setOutgoingWalletAddress(String outgoingWalletAddress) {
        this.outgoingWalletAddress = outgoingWalletAddress;
    }

    public boolean isOutgoing() {
        return outgoing;
    }

    public void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }

    public String getBitcoinAmount() {
        return bitcoinAmount;
    }

    public void setBitcoinAmount(String bitcoinAmount) {
        this.bitcoinAmount = bitcoinAmount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getTotalConfirmation() {
        return totalConfirmation;
    }

    public void setTotalConfirmation(int totalConfirmation) {
        this.totalConfirmation = totalConfirmation;
    }
}
