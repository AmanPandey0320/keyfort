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
@Entity("dimensions")
public class Dimension extends BaseEntity {
	
	@Column(name = "id",define = "UUID DEFAULT gen_random_uuid() PRIMARY KEY")
    private String id;
	
	@Column(name = "name", define = "VARCHAR(256) NOT NULL UNIQUE")
    private String name;
    
    @Column(name = "display_name", define = "VARCHAR(256)")
    private String displayName;
    
    @Column(name = "is_active", define = "BOOLEAN NOT NULL DEFAULT TRUE")
    private boolean isActive;
    
    public Dimension(Row row){
    	this.digest(row, getClass(), this);
    }
    
}
