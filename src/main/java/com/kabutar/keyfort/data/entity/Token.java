package com.kabutar.keyfort.data.entity;

import com.kabutar.keyfort.constant.AuthConstant;
import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;
import com.kabutar.keyfort.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.util.LinkedCaseInsensitiveMap;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity("tokens")
public class Token extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "token", define = "TEXT")
    private String token;

    @Column(name = "type", define = "VARCHAR(64)")
    private String type;

    @Column(name = "valid_till", define = "TIMESTAMP NOT NULL")
    private LocalDateTime validTill;

    @Column(name = "is_valid", define = "BOOLEAN NOT NULL DEFAULT TRUE")
    private boolean isValid;

    @Column(name = "user_id", define = "UUID NOT NULL", reference = "users (id)")
    private UUID userId;

    public Token() {
        this.type = AuthConstant.TokenType.AUTHORIZATION;
    }

    public Token(LinkedCaseInsensitiveMap<Credential> row) {
        this.digest(row, getClass(), this);
    }
}
