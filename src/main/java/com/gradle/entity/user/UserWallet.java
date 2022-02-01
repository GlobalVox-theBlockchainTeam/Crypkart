package com.gradle.entity.user;

import com.gradle.entity.base.BaseModel;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "user_wallets")
public class UserWallet extends BaseModel implements Serializable{

    @Transient
    private static final long serialVersionUID = -1310927231210613207L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;


    @Transient
    private int uid;

    @Column(name = "private_key")
    private String privateKey;

    @Column(name = "allocated_wallet_address")
    private String walletAddress;

    @Column(name = "current_address")
    private boolean currentAddress;

    @Transient
    private String amount;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "confirmation_count", columnDefinition = "int(11) default 0")
    private int totalConfirmation;

    @Transient
    private boolean outgoing;

    @Column(name = "current_transaction_id", columnDefinition = "int(11) default 0")
    private int currentTransactionId;


    @Column(name = "success", nullable = true, columnDefinition = "bit(1) default 0")
    private boolean success;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public boolean isCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(boolean currentAddress) {
        this.currentAddress = currentAddress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public boolean isOutgoing() {
        return outgoing;
    }

    public void setOutgoing(boolean outgoing) {
        this.outgoing = outgoing;
    }

    public int getCurrentTransactionId() {
        return currentTransactionId;
    }

    public void setCurrentTransactionId(int currentTransactionId) {
        this.currentTransactionId = currentTransactionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
