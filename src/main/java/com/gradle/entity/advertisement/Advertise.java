/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.advertisement;

import com.gradle.entity.configurations.AdminConfigValues;
import com.gradle.entity.msg.ChatFiles;
import com.gradle.enums.advertisement.AdType;
import com.gradle.entity.Currency;
import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import com.gradle.util.Common;
import com.gradle.validator.iface.AdvertiseConstraint;
import com.gradle.validator.iface.HtmlValidateConstraint;
import com.opencsv.bean.CsvBindByName;
import org.bitcoin.protocols.payments.Protos;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;

import javax.enterprise.inject.Default;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Advertisement Entity
 * For more details check (advertisement_master table in database)
 */

@Entity
@Table(name = "advertisement_master")
@AdvertiseConstraint
public class Advertise extends BaseModel implements Comparable<Advertise> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int  id;

    @NotEmpty
    @Column(name = "advertisement_id", nullable = false)
    private String advertisementId;


    @NotEmpty
    @Column(name = "advertisement_sequence_id", nullable = false)
    private String advertisementSequenceId;



    @Column(name = "advertisement_type", columnDefinition = "enum('SELL','BUY') default 'SELL'")
    @Enumerated(EnumType.STRING)
    private AdType advertisementType;


    public AdType getAdType() {
        return advertisementType;
    }


    @Type(type = "text")
    @Column(name = "location")
    @HtmlValidateConstraint(whiteListType = "none")
    private String tradeLocation;

    @Column(name = "latitude")
    @HtmlValidateConstraint(whiteListType = "none")
    private String latitude;

    @Column(name = "longitude")
    @HtmlValidateConstraint(whiteListType = "none")
    private String longitude;

    @JoinColumn(name="payment_type_id", nullable=false, referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private PaymentType paymentType;

    @JoinColumn(name="currency_id", nullable=false, referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Currency currency;

    @Column(name = "payment_instructions", columnDefinition = "text")
    @HtmlValidateConstraint(whiteListType = "none")
    private String paymentInstructions;

    /*@NotEmpty
    @Column(name = "amount", nullable = false)*/
    @Transient
    private String amount;

    @CsvBindByName(column = "btcrate")
    @NotEmpty
    @Column(name = "btc_rate", nullable = false)
    @HtmlValidateConstraint(whiteListType = "none")
    private String btcRate;

    @CsvBindByName(column = "margin")
    @HtmlValidateConstraint(whiteListType = "none")
    @Column(name = "margin")
    private String margin;

    @Column(name = "price_equation")
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style","div:style"})
    private String priceEquation;

    @CsvBindByName(column = "min")
    @NotEmpty
    @Column(name = "min_limit")
    @HtmlValidateConstraint(whiteListType = "none")
    private String minLimit;

    @CsvBindByName(column = "max")
    @NotEmpty
    @Column(name = "max_limit")
    @HtmlValidateConstraint(whiteListType = "none")
    private String maxLimit;

    @Column(name = "restricted_amounts")
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style","div:style"})
    private String restrictedAmounts;

    @Column(name = "opening_hours")
    @HtmlValidateConstraint(whiteListType = "none")
    private String openingHours;

    @CsvBindByName(column = "terms")
    @Column(name = "terms_of_trade")
    @Type(type = "text")
    @HtmlValidateConstraint(whiteListType = "none")
    private String termsOfTrade;


    @Column(name = "payment_window")
    @HtmlValidateConstraint(whiteListType = "none")
    private String paymentWindow;

    /*@DefaultValue(value = "120")*/
    @Min(value = 20)
    @Column(name = "timeout", columnDefinition = "int default 120")
    private int timeout;

    @Column(name = "track_liquidity")
    private boolean trackLiquidity;

    @Column(name = "identified_people_only")
    private boolean identifiedPeopleOnly;

    @Column(name = "trusted_people_only")
    private boolean trustedPeopleOnly;


    @Column(name = "sms_verification_required")
    private boolean smsVerificationRequired;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id", nullable=false, referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "advertise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Trade> tradeList = new ArrayList<Trade>();

    /**
     *
     * Below Transient fields can only be used for csv export
     */
    @CsvBindByName(column = "username")
    @Transient
    private String username;
    @CsvBindByName(column = "paymenttypename")
    @Transient
    private String paymentTypeName;
    @CsvBindByName(column = "currencyname")
    @Transient
    private String currencyName;
    @CsvBindByName(column = "advertisetype")
    @Transient
    private String advertiseType;




    @Column(name = "hidden", columnDefinition = "bit(1) default 0")
    private boolean hidden;

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getAdvertiseType() {
        return this.advertisementType.getValue();
    }

    public String getUsername() {
        return this.user.getUsername();
    }

    public String getPaymentTypeName() {
        return this.paymentType.getPaymentTypeName();
    }

    public String getCurrencyName() {
        return this.currency.getCurrencyName();
    }

    /**
     *
     * *****************************
     */


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AdType getAdvertisementType() {
        return advertisementType;
    }

    public void setAdvertisementType(AdType advertisementType) {
        this.advertisementType = advertisementType;
    }

    public String getTradeLocation() {
        return tradeLocation;
    }

    public void setTradeLocation(String tradeLocation) {
        this.tradeLocation = tradeLocation;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getPaymentInstructions() {
        return paymentInstructions;
    }

    public void setPaymentInstructions(String paymentInstructions) {
        this.paymentInstructions = paymentInstructions;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getPriceEquation() {
        return priceEquation;
    }

    public void setPriceEquation(String priceEquation) {
        this.priceEquation = priceEquation;
    }

    public String getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(String minLimit) {
        this.minLimit = minLimit;
    }

    public String getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(String maxLimit) {
        this.maxLimit = maxLimit;
    }

    public String getRestrictedAmounts() {
        return restrictedAmounts;
    }

    public void setRestrictedAmounts(String restrictedAmounts) {
        this.restrictedAmounts = restrictedAmounts;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openinGhours) {
        this.openingHours = openinGhours;
    }

    public String getTermsOfTrade() {

        return termsOfTrade;

    }

    public void setTermsOfTrade(String termsOfTrade) {
        this.termsOfTrade = termsOfTrade;
    }

    public String getPaymentWindow() {
        return paymentWindow;
    }

    public void setPaymentWindow(String paymentWindow) {
        this.paymentWindow = paymentWindow;
    }

    public boolean isTrackLiquidity() {
        return trackLiquidity;
    }

    public void setTrackLiquidity(boolean trackLiquidity) {
        this.trackLiquidity = trackLiquidity;
    }

    public boolean isIdentifiedPeopleOnly() {
        return identifiedPeopleOnly;
    }

    public void setIdentifiedPeopleOnly(boolean identifiedPeopleOnly) {
        this.identifiedPeopleOnly = identifiedPeopleOnly;
    }

    public boolean isTrustedPeopleOnly() {
        return trustedPeopleOnly;
    }

    public void setTrustedPeopleOnly(boolean trustedPeopleOnly) {
        this.trustedPeopleOnly = trustedPeopleOnly;
    }

    public boolean isSmsVerificationRequired() {
        return smsVerificationRequired;
    }

    public void setSmsVerificationRequired(boolean smsVerificationRequired) {
        this.smsVerificationRequired = smsVerificationRequired;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBtcRate() {
        return btcRate;
    }

    public void setBtcRate(String btcRate) {
        this.btcRate = btcRate;
    }

    public List<Trade> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<Trade> tradeList) {
        this.tradeList = tradeList;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getAdvertisementId() {
        return advertisementId;
    }

    public void setAdvertisementId(String advertisementId) {
        this.advertisementId = advertisementId;
    }

    public String getAdvertisementSequenceId() {
        return advertisementSequenceId;
    }

    public void setAdvertisementSequenceId(String advertisementSequenceId) {
        this.advertisementSequenceId = advertisementSequenceId;
    }

    @Override
    public int compareTo(Advertise advertise) {
        String amount1 = (Common.plainStringPrice(advertise.getBtcRate()));
        String amount2 = (Common.plainStringPrice(this.getBtcRate()));
        return Integer.parseInt(amount1)- Integer.parseInt(amount2);
    }

    public static Comparator<Advertise> AdvertiseDescComparator = new Comparator<Advertise>() {
        @Override
        public int compare(Advertise advertise, Advertise t1) {
            Double amount1 = Double.valueOf(Common.plainStringPrice(advertise.getBtcRate())).doubleValue();
            Double amount2 = Double.valueOf(Common.plainStringPrice(t1.getBtcRate())).doubleValue();

            return amount2.compareTo(amount1);
        }
    };

    public static Comparator<Advertise> AdvertiseAscComparator = new Comparator<Advertise>() {
        @Override
        public int compare(Advertise advertise, Advertise t1) {
            Double amount1 = Double.valueOf(Common.plainStringPrice(advertise.getBtcRate())).doubleValue();
            Double amount2 = Double.valueOf(Common.plainStringPrice(t1.getBtcRate())).doubleValue();
            return amount1.compareTo(amount2);
        }
    };


}

