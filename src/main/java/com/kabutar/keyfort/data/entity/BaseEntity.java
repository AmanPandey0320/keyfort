package com.kabutar.keyfort.data.entity;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * BaseEntity provides common fields and utility for all entities. It includes auditing fields
 * (creation/update timestamps and user info) and a reflective digest method to map R2DBC rows to
 * entity fields. @MappedSuperclass indicates that this class is not an entity itself, but its
 * mapping information applies to its subclasses.
 */
@Getter
@Setter
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

    protected LinkedCaseInsensitiveMap<?> extraFields;

    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        this.createdBy = "SYSTEM";
        this.updatedBy = "SYSTEM";

        this.deletedAt = null;
        this.isDeleted = false;
    }

    // to get values from extra fields
    public Object get(String key) {
        return this.extraFields.get(key);
    }

    // overloading for Map<String, Object> row
    protected void digest(LinkedCaseInsensitiveMap<?> row, Class<?> clazz, BaseEntity object) {
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    field.setAccessible(true);
                    try {
                        field.set(object, row.get(column.name()));
                        row.remove(column.name());
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        logger.error(
                                "Error while digesting: {} for class {}, reason: {}",
                                field.getName(),
                                clazz.getName(),
                                e.getMessage());
                        logger.debug(e);
                    }
                    field.setAccessible(false);

                } else if (field.isAnnotationPresent(Id.class)) {
                    Id column = field.getAnnotation(Id.class);

                    field.setAccessible(true);
                    try {
                        field.set(object, row.get(column.name()));
                        row.remove(column.name());
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        logger.error(
                                "Error while digesting: {} for class {}, reason: {}",
                                field.getName(),
                                clazz.getName(),
                                e.getMessage());
                        logger.debug(e);
                    }
                    field.setAccessible(false);
                }
            }
            clazz = clazz.getSuperclass();
        }
        object.setExtraFields(row);
    }
}
