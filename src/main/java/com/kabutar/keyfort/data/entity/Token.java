package com.kabutar.keyfort.data.entity;

import com.kabutar.keyfort.constant.AuthConstant;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Date;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String token;

    @Column(nullable = false)
    private String type = AuthConstant.TokenType.AUTHORIZATION;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    private Timestamp validTill;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isValid = true;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private DepUser user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getValidTill() {
        return validTill;
    }

    public void setValidTill(Timestamp validTill) {
        this.validTill = validTill;
    }

    public DepUser getUser() {
        return user;
    }

    public void setUser(DepUser user) {
        this.user = user;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", validTill=" + validTill +
                ", isValid=" + isValid +
                ", user=" + user +
                '}';
    }
}
