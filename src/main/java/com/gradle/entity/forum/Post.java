/*
 * Copyright (c) 8/3/18 10:26 AM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

/**
 * Created by Dawid Stankiewicz on 17.07.2016
 */
package com.gradle.entity.forum;

import com.gradle.entity.base.BaseModel;
import com.gradle.entity.user.User;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "forum_post")
@DynamicUpdate
public class Post extends BaseModel implements Serializable {

    private static final long serialVersionUID = 4235393151425571253L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "topic_id", nullable = false, referencedColumnName = "id")
    private Topic topic;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Override
    public void preDelete() {

        this.createdAt = null;

    }


    public Post() {
    }


    public int getId() {
        return id;
    }

    public void setId(int idPost) {
        this.id = idPost;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + id;
        result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Post other = (Post) obj;
        if (content == null) {
            if (other.content != null) {
                return false;
            }
        } else if (!content.equals(other.content)) {
            return false;
        }
        if (createdAt == null) {
            if (other.createdAt != null) {
                return false;
            }
        } else if (!createdAt.equals(other.createdAt)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (updatedAt == null) {
            if (other.updatedAt != null) {
                return false;
            }
        } else if (!updatedAt.equals(other.updatedAt)) {
            return false;
        }
        /*if (topic == null) {
            if (other.topic != null) {
                return false;
            }
        } else if (!topic.equals(other.topic)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }*/
        return true;
    }


}
