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

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "forum_topics")
public class Topic extends BaseModel implements  Serializable {

    private static final long serialVersionUID = -1722083052479276312L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "section_id", nullable = false, referencedColumnName = "id")
    private Section section;


    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> postList = new ArrayList<Post>();



    @Column(length = 50)
    private String title;


    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private int views;



    @Column
    private boolean closed;

    public Topic() {
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }



    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (closed ? 1231 : 1237);
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + id;
        result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
        result = prime * result + ((section == null) ? 0 : section.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + views;
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
        Topic other = (Topic) obj;
        if (closed != other.closed) {
            return false;
        }
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
        if (section == null) {
            if (other.section != null) {
                return false;
            }
        } else if (!section.equals(other.section)) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }
        if (views != other.views) {
            return false;
        }
        return true;
    }
}
