/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity;

import com.gradle.entity.advertisement.Advertise;
import com.gradle.entity.advertisement.Trade;
import com.gradle.entity.base.BaseModel;
import com.gradle.entity.msg.ChatHistory;
import com.gradle.entity.user.User;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Currency Entity
 * For more details check (currency table in database)
 */
@Entity
@Table(name = "currency")
public class Currency extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @NotEmpty
    @Column(name = "currency_code", nullable = false)
    @HtmlValidateConstraint(whiteListType = "none")
    private String currencyCode;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "dial_code")
    private String dialCode;

    @NotEmpty
    @Column(name = "currency_name", nullable = false)
    @HtmlValidateConstraint(whiteListType = "none")
    private String currencyName;


    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<User> userList;




/*@OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Advertise> List = new ArrayList<Advertise>();*/

    /*public java.util.List<Advertise> getList() {
        return List;
    }

    public void setList(java.util.List<Advertise> list) {
        List = list;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }
}
