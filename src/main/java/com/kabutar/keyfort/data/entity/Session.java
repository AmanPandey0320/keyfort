package com.kabutar.keyfort.data.entity;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;
import com.kabutar.keyfort.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.util.LinkedCaseInsensitiveMap;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity("sessions")
public class Session extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "is_Authenticated", define = "BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean isAuthenticated;

    @Column(name = "last_used", define = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime lastUsed;

    @Column(name = "user_id", define = "UUID", reference = "users (id)")
    private UUID userId;

    public Session(LinkedCaseInsensitiveMap<Client> row) {
        this.digest(row, getClass(), this);
    }
}
