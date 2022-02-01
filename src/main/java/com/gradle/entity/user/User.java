/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.user;

import com.gradle.entity.Currency;
import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.base.BaseModel;
import com.gradle.entity.configurations.AdminConfig;
import com.gradle.entity.forum.Post;
import com.gradle.entity.forum.Topic;
import com.gradle.entity.frontend.CMS;
import com.gradle.entity.msg.ChatFiles;
import com.gradle.entity.msg.ChatHistory;
import com.gradle.validator.iface.HtmlValidateConstraint;
import com.gradle.validator.iface.UsernameConstraint;
import com.opencsv.bean.CsvBindByName;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.jboss.logging.annotations.Message;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * User Entity
 * For more details check (User table in database)
 */
@Table(name = "users")
@UsernameConstraint
@DynamicUpdate
@Entity
public class User extends BaseModel implements Cloneable, Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @CsvBindByName(column = "username")
    @NotEmpty
    @Column(name = "user_name", nullable = false, unique = true)
    @HtmlValidateConstraint(whiteListType = "none")
    private String username;


    @Column(name = "wallet_file_path", columnDefinition = "text")
    private String walletFile;


    @Column(name = "password", nullable = false)
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style", "div:style"})
    private String password;

    @Transient
    private String confirmPassword;

    @Transient
    private String oldPassword;


    @ManyToOne(targetEntity = Zones.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "zone_id")
    private Zones zone;

    @ManyToOne(targetEntity = Currency.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "currency_id")
    private Currency currency;


    @NotNull
    @ManyToOne(targetEntity = Countries.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "country_id")
    private Countries country;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CMS> cmsList;

    @OneToMany(mappedBy = "trader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Trade> tradeList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Trade> userList;


    @OneToMany(mappedBy = "userTo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChatHistory> userFromList;

    @OneToMany(mappedBy = "userFrom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChatHistory> userToList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Advertise> advertiseList;


    @OneToMany(mappedBy = "userFrom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChatFiles> chatUserFromFilesList;

    @OneToMany(mappedBy = "userTo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChatFiles> chatUserToFilesList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Post> postList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Topic> topicList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<AdminConfig> adminConfigs;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<FeedBack> fromFeedBacks;

    @OneToMany(mappedBy = "userTo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<FeedBack> toFeedBacks;


    @CsvBindByName(column = "firstname")
    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false)
    @HtmlValidateConstraint(whiteListType = "none")
    private String firstName;


    @Size(max = 50)
    @Column(name = "last_name")
    @HtmlValidateConstraint(whiteListType = "none")
    private String lastName;

    @Size(max = 50)
    @Column(name = "middle_name")
    @HtmlValidateConstraint(whiteListType = "none")
    private String middleName;

    @Email
    @NotNull
    @NotEmpty
    @Column(name = "email", nullable = false)
    @CsvBindByName(column = "email")
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style", "div:style"})
    private String email;

    @CsvBindByName(column = "Country Code")
    /*@Size(min = 2, max = 2)*/
    @Column(name = "country_code")
    @HtmlValidateConstraint(whiteListType = "none")
    private String countryCode;

    @CsvBindByName(column = "phone")
    /*@Size(min = 7, max = 12)*/
    @Column(name = "phone")
    @HtmlValidateConstraint(whiteListType = "none")
    private String phone;

    @Column(name = "user_time_zone")
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style", "div:style"})
    private String userTimeZone;


    @CsvBindByName(column = "address")
    @Column(/*columnDefinition = "varchar(255) default 'Default Address'"*/ nullable = false)
    @HtmlValidateConstraint(whiteListType = "none")
    private String address;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @BatchSize(size = 500)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    public List<Role> roles;

    @Transient
    @CsvBindByName(column = "ROLES")
    @HtmlValidateConstraint(whiteListType = "none")
    private String userroles;


    @Column(name = "enabled", columnDefinition = "bit(1) default 0")
    private boolean enabled;


    @Column(name = "phone_verified")
    private boolean phoneVerified = false;

    @Column(name = "trusted")
    private boolean trusted = false;


    @Column(name = "selling_vacation")
    private boolean sellingVacation = false;

    @Column(name = "buying_vacation")
    private boolean buyingVacation = false;

    @Column(name = "enable_web_notifications")
    private boolean enableWebNotification = true;

    @Column(name = "email_sensitive_information")
    private boolean disableSensitiveInformationFromEmail = false;

    @Column(name = "account_deleted")
    private boolean accountDeleted = false;

    @Column(name = "new_trade_sms")
    private boolean sendNewTradeSms = false;

    @Column(name = "escrow_sms")
    private boolean escrowSms = false;


    @Column(name = "final_bitcoin_amount")
    @HtmlValidateConstraint(whiteListType = "none")
    private String finalBitcoinAmount;

    @Column(name = "bitcoin_in_escrow")
    @HtmlValidateConstraint(whiteListType = "none")
    private String bitcoinsInEscrow;


    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "parent_id", nullable = true, columnDefinition = "int(11) default NULL")
    private User parent;


    @Column(name = "google_authenticator_key")
    private String googleAuthenticatorKey;


    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "last_seen_at")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime lastSeenAt;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "last_login_at")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime lastLoginAt;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "current_login_at")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime currentLoginAt;


    @Column(name = "avg_chat_response_time")
    private String averageResponseTime;


    @Column(name = "lastLoginIp")
    private String lastLoginIp;

    public String getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(String averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public LocalDateTime getCurrentLoginAt() {
        return currentLoginAt;
    }

    public void setCurrentLoginAt(LocalDateTime currentLoginAt) {
        this.currentLoginAt = currentLoginAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public String getBitcoinsInEscrow() {
        return bitcoinsInEscrow;
    }

    public void setBitcoinsInEscrow(String bitcoinsInEscrow) {
        this.bitcoinsInEscrow = bitcoinsInEscrow;
    }

    public String getFinalBitcoinAmount() {
        return finalBitcoinAmount;
    }

    public void setFinalBitcoinAmount(String finalBitcoinAmount) {
        this.finalBitcoinAmount = finalBitcoinAmount;
    }

    public User() {
        super();
        this.enabled = false;
    }


    public Set<FeedBack> getFromFeedBacks() {
        return fromFeedBacks;
    }

    public void setFromFeedBacks(Set<FeedBack> fromFeedBacks) {
        this.fromFeedBacks = fromFeedBacks;
    }

    public Set<FeedBack> getToFeedBacks() {
        return toFeedBacks;
    }

    public void setToFeedBacks(Set<FeedBack> toFeedBacks) {
        this.toFeedBacks = toFeedBacks;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public boolean isTrusted() {
        return trusted;
    }

    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUserTimeZone() {
        return userTimeZone;
    }

    public void setUserTimeZone(String userTimeZone) {
        this.userTimeZone = userTimeZone;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @Override
    @PrePersist
    public void preInsert() {
        super.preInsert();
        this.finalBitcoinAmount = "1";
        if (getAddress() == null) {
            this.address = "default";
        }

    }

    @Override
    @PreUpdate
    public void preUpdate() {
        super.preUpdate();
        if (getAddress() == null) {
            this.address = "default";
        }
    }


    public Set<Trade> getTradeList() {
        return tradeList;
    }

    public void setTradeList(Set<Trade> tradeList) {
        this.tradeList = tradeList;
    }

    public Set<Trade> getUserList() {
        return userList;
    }

    public void setUserList(Set<Trade> userList) {
        this.userList = userList;
    }

    public Set<ChatHistory> getUserFromList() {
        return userFromList;
    }

    public void setUserFromList(Set<ChatHistory> userFromList) {
        this.userFromList = userFromList;
    }

    public Set<ChatHistory> getUserToList() {
        return userToList;
    }

    public void setUserToList(Set<ChatHistory> userToList) {
        this.userToList = userToList;
    }


    public Set<Advertise> getAdvertiseList() {
        return advertiseList;
    }

    public void setAdvertiseList(Set<Advertise> advertiseList) {
        this.advertiseList = advertiseList;
    }

    public Set<ChatFiles> getChatUserFromFilesList() {
        return chatUserFromFilesList;
    }

    public void setChatUserFromFilesList(Set<ChatFiles> chatUserFromFilesList) {
        this.chatUserFromFilesList = chatUserFromFilesList;
    }

    public Set<ChatFiles> getChatUserToFilesList() {
        return chatUserToFilesList;
    }

    public void setChatUserToFilesList(Set<ChatFiles> chatUserToFilesList) {
        this.chatUserToFilesList = chatUserToFilesList;
    }

    public Set<CMS> getCmsList() {
        return cmsList;
    }

    public void setCmsList(Set<CMS> cmsList) {
        this.cmsList = cmsList;
    }

    public Set<Post> getPostList() {
        return postList;
    }

    public void setPostList(Set<Post> postList) {
        this.postList = postList;

    }

    public Set<Topic> getTopicList() {
        return topicList;
    }

    public void setTopicList(Set<Topic> topicList) {
        this.topicList = topicList;
    }

    public Set<AdminConfig> getAdminConfigs() {
        return adminConfigs;
    }

    public void setAdminConfigs(Set<AdminConfig> adminConfigs) {
        this.adminConfigs = adminConfigs;
    }

    public Zones getZone() {
        return zone;
    }

    public void setZone(Zones zone) {
        this.zone = zone;
    }

    public Countries getCountry() {
        return country;
    }

    public void setCountry(Countries country) {
        this.country = country;
    }


    public boolean isSellingVacation() {
        return sellingVacation;
    }

    public void setSellingVacation(boolean sellingVacation) {
        this.sellingVacation = sellingVacation;
    }

    public boolean isBuyingVacation() {
        return buyingVacation;
    }

    public void setBuyingVacation(boolean buyingVacation) {
        this.buyingVacation = buyingVacation;
    }

    public boolean isEnableWebNotification() {
        return enableWebNotification;
    }

    public void setEnableWebNotification(boolean enableWebNotification) {
        this.enableWebNotification = enableWebNotification;
    }

    public boolean isDisableSensitiveInformationFromEmail() {
        return disableSensitiveInformationFromEmail;
    }

    public void setDisableSensitiveInformationFromEmail(boolean disableSensitiveInformationFromEmail) {
        this.disableSensitiveInformationFromEmail = disableSensitiveInformationFromEmail;
    }

    public boolean isAccountDeleted() {
        return accountDeleted;
    }

    public void setAccountDeleted(boolean accountDeleted) {
        this.accountDeleted = accountDeleted;
    }

    public boolean isSendNewTradeSms() {
        return sendNewTradeSms;
    }

    public void setSendNewTradeSms(boolean sendNewTradeSms) {
        this.sendNewTradeSms = sendNewTradeSms;
    }

    public boolean isEscrowSms() {
        return escrowSms;
    }

    public void setEscrowSms(boolean escrowSms) {
        this.escrowSms = escrowSms;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }


    public String getGoogleAuthenticatorKey() {
        return googleAuthenticatorKey;
    }

    public void setGoogleAuthenticatorKey(String googleAuthenticatorKey) {
        this.googleAuthenticatorKey = googleAuthenticatorKey;
    }


    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public String getUserroles() {
        List<Role> roles = this.roles;
        String strRoles = "";
        if (roles != null) {
            for (Role role : roles) {
                strRoles += role.getRole() + ", ";
            }
        }
        return strRoles;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, username);
    }

    public String getWalletFile() {
        return walletFile;
    }

    public void setWalletFile(String walletFile) {
        this.walletFile = walletFile;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /*public User clone() throws CloneNotSupportedException {
        User user = (User)super.clone();
        user.tradeList = this.tradeList;
        user.advertiseList=this.advertiseList;
        return user;
    }*/






}


