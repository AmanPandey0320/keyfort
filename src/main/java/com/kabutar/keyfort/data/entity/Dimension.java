package com.kabutar.keyfort.data.entity;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;
import com.kabutar.keyfort.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity("dimensions")
@NoArgsConstructor
public class Dimension extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "name", define = "VARCHAR(256) NOT NULL UNIQUE")
    private String name;

    @Column(name = "display_name", define = "VARCHAR(256)")
    private String displayName;

    @Column(name = "is_active", define = "BOOLEAN NOT NULL DEFAULT TRUE")
    private Boolean isActive;

    public Dimension(LinkedCaseInsensitiveMap<Dimension> row) {
        this.digest(row, getClass(), this);
    }
}
