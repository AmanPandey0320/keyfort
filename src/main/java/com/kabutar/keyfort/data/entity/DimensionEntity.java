package com.kabutar.keyfort.data.entity;

import io.r2dbc.spi.Row;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DimensionEntity extends BaseEntity {
	
	@ColumnName("id")
    private String id;
	
	@ColumnName("name")
    private String name;
    
    @ColumnName("display_name")
    private String displayName;
    
    @ColumnName("is_active")
    private boolean isActive;
    
    public static DimensionEntity getEntity(Row row) {
    	DimensionEntity entity  = new DimensionEntity();
    	entity.digest(row, DimensionEntity.class, entity);
    	return entity;
    }
    
    

}
