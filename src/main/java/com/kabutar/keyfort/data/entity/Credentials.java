package com.kabutar.keyfort.data.entity;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;
import com.kabutar.keyfort.data.annotation.Id;

import io.r2dbc.spi.Row;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity("credentials")
public class Credentials extends BaseEntity {

	@Id
    private String id;

	@Column(name = "hash",define = "TEXT NOT NULL")
    private String hash;

	@Column(name = "is_active", define = "BOOLEAN NOT NULL DEFAULT TRUE")
    private Boolean isActive;

	@Column(name = "user_id", define = "UUID NOT NULL", reference = "users (id)")
    private String userId;
	
	public Credentials(Row row) {
		this.digest(row, getClass(), this);
	}
}
