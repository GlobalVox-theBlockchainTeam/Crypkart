/*
 * Copyright (c) 5/3/18 11:15 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.entity.frontend;

import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import com.gradle.validator.iface.CMSConstraint;
import com.gradle.validator.iface.HtmlValidateConstraint;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "cms_master")
@DynamicUpdate
@CMSConstraint
public class CMS extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotEmpty
    @Column(name = "content", columnDefinition = "text", nullable = false)
    @HtmlValidateConstraint(whiteListType = "basic", addAttributes = {"span:style","div:style"})
    private String content;

    @NotEmpty
    @Column(name = "name", nullable = false)
    @Size(max = 255, min = 5)
    @HtmlValidateConstraint(whiteListType = "none")
    private String name;


    @Column(name = "page_id", nullable = false, unique = true)
    @HtmlValidateConstraint(whiteListType = "none")
    private String pageId;


    @Column(name = "page_title_code")
    @HtmlValidateConstraint(whiteListType = "none")
    private String pageTitleCode;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id", nullable=false, referencedColumnName = "id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getPageTitleCode() {
        return pageTitleCode;
    }

    public void setPageTitleCode(String pageTitleCode) {
        this.pageTitleCode = pageTitleCode;
    }
}
