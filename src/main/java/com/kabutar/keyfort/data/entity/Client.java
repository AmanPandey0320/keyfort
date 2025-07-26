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
@Entity("clients")
public class Client extends BaseEntity {
	
	@Column(name = "id",define = "VARCHAR(256) PRIMARY KEY")
	private String id;
	
	@Column(name = "secret", define = "VARCHAR(512) NOT NULL")
	private String secret;
	
	@Column(name = "grant_type", define = "VARCHAR(32) NOT NULL DEFAULT 'token'")
	private String grantType;
	
	@Column(name = "redirect_uri", define = "VARCHAR(2048)")
	private String redirectUri;
	
	@Column(name = "name", define = "VARCHAR(64)")
	private String name;
	
	@Column(name = "dimension_id", define = "VARCHAR(256) NOT NULL")
	private String dimensionId;

	public Client(Row row){
    	this.digest(row, getClass(), this);
    }
	

}
