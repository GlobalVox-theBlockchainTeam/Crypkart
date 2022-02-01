/*
 * Copyright (c) 12/3/18 10:13 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.configurations;

import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import com.gradle.validator.iface.AdminConfigConstraint;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AdminConfigConstraint
@Entity
@Table(name = "admin_config")
public class AdminConfig  extends BaseModel{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @NotEmpty(message = "Can not be empty!")
    @Column(name = "config_name", nullable = false)
    private String configName;

    @Column(name = "data_table")
    @HtmlValidateConstraint(whiteListType = "none")
    private String type;

    @Column(name = "upload_path", columnDefinition = "text")
    @HtmlValidateConstraint(whiteListType = "simpleText", addAttributes = {"span:style","div:style"})
    private String uploadPath;

    @Column(name = "record_per_page")
    private int recordPerPage;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "adminConfig")
    private List<AdminConfigValues> adminConfigValuesList = new ArrayList<AdminConfigValues>();




    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;


    @Override
    @PrePersist
    public void preInsert(){
        this.status= true;
        this.createdAt = new LocalDateTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public int getRecordPerPage() {
        return recordPerPage;
    }

    public void setRecordPerPage(int recordPerPage) {
        this.recordPerPage = recordPerPage;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public List<AdminConfigValues> getAdminConfigValuesList() {
        return adminConfigValuesList;
    }

    public void setAdminConfigValuesList(List<AdminConfigValues> adminConfigValuesList) {
        this.adminConfigValuesList = adminConfigValuesList;
    }

}

