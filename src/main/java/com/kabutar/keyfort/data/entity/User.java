package com.kabutar.keyfort.data.entity;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;
import com.kabutar.keyfort.data.annotation.Id;

import io.r2dbc.spi.Row;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.UUID;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity("users")
public class User extends BaseEntity {
	
	@Id
    private UUID id;

	@Column(name = "username",define = "VARCHAR(255) NOT NULL UNIQUE")
    private String username;

	@Column(name = "email",define = "VARCHAR(255) NOT NULL UNIQUE")
    private String email;

	@Column(name = "first_name",define = "VARCHAR(255) NOT NULL")
    private String firstName;

	@Column(name = "last_name",define = "VARCHAR(255) NOT NULL")
    private String lastName;
	
	@Column(name = "middle_name",define = "VARCHAR(255)")
	private String middleName;

	@Column(name = "is_verified",define = "BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean isVerified = false;
	
	@Column(name = "client_id", define = "UUID NOT NULL", reference = "clients (id)")
	private UUID clientId;

    public User(LinkedCaseInsensitiveMap<User> row) {
        this.digest(row, getClass(), this);
    }

}
