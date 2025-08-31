package com.kabutar.keyfort.data.entity;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Id;
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

    @Column(name = "created_at", define = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    protected LocalDateTime createdAt;

    @Column(name = "updated_at", define = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    protected LocalDateTime updatedAt;

    @Column(name = "created_by", define = "VARCHAR(255)")
    protected String createdBy;

    @Column(name = "updated_by", define = "VARCHAR(255)")
    protected String updatedBy;
    
    @Column(name = "deleted_at", define = "TIMESTAMP")
    protected LocalDateTime deletedAt;
    
    @Column(name = "is_deleted", define = "BOOLEAN NOT NULL DEFAULT FALSE")
    protected Boolean isDeleted;
    
    /**
     * Populates the fields of a {@link BaseEntity} object from a database row.
     * <p>
     * This method iterates through the fields of the given `clazz` (and its superclasses)
     * looking for fields annotated with {@link Column}. For each such field, it attempts
     * to retrieve the corresponding value from the provided {@link Row} object using the
     * column name defined in the {@link Column} annotation and the field's type.
     * </p>
     * <p>
     * Field accessibility is temporarily set to `true` to allow setting private fields
     * and then restored to `false`. Any {@code IllegalArgumentException} or
     * {@code IllegalAccessException} encountered during field setting is caught and logged
     * as an error, with debug-level logging for the exception details.
     * </p>
     *
     * @param row The {@link Row} object containing the data retrieved from the database.
     * @param clazz The {@code Class<?>} object representing the current class or superclass
     * being processed in the entity hierarchy.
     * @param object The {@link BaseEntity} instance whose fields are to be populated.
     * @see Row
     * @see BaseEntity
     * @see Column
     */
	protected void digest(Row row,Class<?> clazz,BaseEntity object) {
		while(clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(Field field:fields) {
				if(field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					field.setAccessible(true);
					try {
						field.set(object, row.get(column.name(),field.getType()));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						logger.error("Error while digesting: {} for class {}",field.getName(),clazz.getName());
						logger.debug(e);
					}
					field.setAccessible(false);
					
				}else if(field.isAnnotationPresent(Id.class)) {
					Id column = field.getAnnotation(Id.class);
					field.setAccessible(true);
					try {
						field.set(object, row.get(column.name(),field.getType()));
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
	
	// overloading for Map<String, Object> row
	protected void digest(LinkedCaseInsensitiveMap<?> row,Class<?> clazz,BaseEntity object) {
		while(clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(Field field:fields) {
				if(field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					field.setAccessible(true);
					try {
						field.set(object, row.get(column.name()));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						logger.error("Error while digesting: {} for class {}, reason: {}",field.getName(),clazz.getName(),e.getMessage());
						logger.debug(e);
					}
					field.setAccessible(false);
					
				}else if(field.isAnnotationPresent(Id.class)) {
					Id column = field.getAnnotation(Id.class);
	
					field.setAccessible(true);
					try {
						field.set(object, row.get(column.name()));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						logger.error("Error while digesting: {} for class {}, reason: {}",field.getName(),clazz.getName(),e.getMessage());
						logger.debug(e);
					}
					field.setAccessible(false);
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
}
