/*
 * Copyright (c) 12/3/18 12:13 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.configurations;

import com.gradle.entity.base.BaseModel;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "admin_config_values")
public class AdminConfigValues extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotEmpty
    @Column(name = "config_name")
    @HtmlValidateConstraint(whiteListType = "none")
    private String name;

    @NotNull(message = "Please select admin config")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id",name = "admin_config_id")
    private AdminConfig adminConfig;

    @NotEmpty
    @Column(name = "config_value", columnDefinition = "text")
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style","div:style"})
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public AdminConfig getAdminConfig() {
        return adminConfig;
    }

    public void setAdminConfig(AdminConfig adminConfig) {
        this.adminConfig = adminConfig;
    }

    @Override
    @PrePersist
    public void preInsert(){
        this.status= true;
        this.createdAt = new LocalDateTime();
    }
}
