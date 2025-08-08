package com.kabutar.keyfort.data.entity;

import java.time.LocalDateTime;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;
import com.kabutar.keyfort.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity("sessions")
public class Session extends BaseEntity {

	@Id
	private String id;
	
	@Column(name = "is_Authenticated",define = "BOOLEAN NOT NULL DEFAULT FALSE")
	private boolean isAuthenticated;
	
	@Column(name = "last_used", define = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime lastUsed;
	
	@Column(name = "user_id", define = "UUID NOT NULL", reference = "users (id)")
	private String userId;
}
