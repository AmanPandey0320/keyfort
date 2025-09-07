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
@Entity("clients")
public class Client extends BaseEntity {
	
	@Id
	private UUID id;
	
	@Column(name = "secret", define = "VARCHAR(512) NOT NULL")
	private String secret;
	
	@Column(name = "grant_type", define = "VARCHAR(32) NOT NULL DEFAULT 'token'")
	private String grantType;
	
	@Column(name = "redirect_uri", define = "VARCHAR(2048)")
	private String redirectUri;
	
	@Column(name = "name", define = "VARCHAR(64)")
	private String name;
	
	@Column(name = "dimension_id", define = "UUID NOT NULL", reference = "dimensions (id)")
	private UUID dimensionId;

	public Client(Row row){
    	this.digest(row, getClass(), this);
    }

    public Client(LinkedCaseInsensitiveMap<Client> row) {
        this.digest(row, getClass(), this);
    }

}
