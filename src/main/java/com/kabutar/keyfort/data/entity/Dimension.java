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
@Entity("dimensions")
public class Dimension extends BaseEntity {
	
	@Id
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
