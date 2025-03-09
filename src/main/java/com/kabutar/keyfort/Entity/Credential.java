package com.kabutar.keyfort.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String hash;

    private Boolean isActive;

    private Boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User User;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public com.kabutar.keyfort.Entity.User getUser() {
        return User;
    }

    public void setUser(com.kabutar.keyfort.Entity.User user) {
        User = user;
    }
}
