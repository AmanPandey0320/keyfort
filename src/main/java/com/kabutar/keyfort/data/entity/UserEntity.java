package com.kabutar.keyfort.data.entity;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;

import io.r2dbc.spi.Row;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity("users")
public class UserEntity extends BaseEntity {
	
	@Column(name = "id",define = "VARCHAR(256) PRIMARY KEY")
    private String id;
	
	@Column(name = "username",define = "VARCHAR(128) NOT NULL UNIQUE")
    private String username;
	
	@Column(name = "email",define = "VARCHAR(256) NOT NULL UNIQUE")
    private String email;
	
	@Column(name = "first_name",define = "VARCHAR(64)")
    private String firstName;
	
	@Column(name = "last_name",define = "VARCHAR(64)")
    private String lastName;
	
	@Column(name = "is_active", define = "BOOLEAN NOT NULL DEFAULT TRUE")
    private boolean isActive;

	public UserEntity(Row row) {
		this.digest(row, getClass(), this);
	}
	
	
    
    
}
