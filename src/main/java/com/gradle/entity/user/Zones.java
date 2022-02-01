/*
 * Copyright (c) 3/4/18 11:03 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.user;

import com.gradle.entity.base.BaseModel;
import com.gradle.entity.msg.ChatHistory;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "zone")
public class Zones extends BaseModel {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "zone_id")
    private int zoneId;

    @Column(name = "country_code")
    @HtmlValidateConstraint(whiteListType = "none")
    private String countryCode;

    @Column(name = "zone_name")
    @HtmlValidateConstraint(whiteListType = "none")
    private String name;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<User> users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
