package com.kabutar.keyfort.data.entity;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kabutar.keyfort.security.service.AuthService;

import io.r2dbc.spi.Row;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * BaseEntity provides common fields and utility for all entities.
 * It includes auditing fields (creation/update timestamps and user info)
 * and a reflective digest method to map R2DBC rows to entity fields.
 *
 * @MappedSuperclass indicates that this class is not an entity itself,
 * but its mapping information applies to its subclasses.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class BaseEntity {
	private final Logger logger = LogManager.getLogger(BaseEntity.class);

    @ColumnName("created_at")
    protected LocalDateTime createdAt;

    @ColumnName("updated_at")
    protected LocalDateTime updatedAt;

    @ColumnName("created_by")
    protected String createdBy;

    @ColumnName("updated_by")
    protected String updatedBy;
    
	protected void digest(Row row,Class<?> clazz,BaseEntity object) {
		while(clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(Field field:fields) {
				if(field.isAnnotationPresent(ColumnName.class)) {
					ColumnName column = field.getAnnotation(ColumnName.class);
					field.setAccessible(true);
					try {
						field.set(object, row.get(column.value(),field.getType()));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						logger.error("Error while digesting: {} for class {}",field.getName(),clazz.getName());
						logger.debug(e);
					}
					field.setAccessible(false);
					
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
}
