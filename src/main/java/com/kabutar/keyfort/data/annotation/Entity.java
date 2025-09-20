package com.kabutar.keyfort.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an annotation to mark a class as a database entity.
 *
 * <p>This annotation is applied to classes that represent a table in a relational database. It
 * provides the name of the database table to which the entity class maps.
 *
 * <p>The annotation has {@link RetentionPolicy#RUNTIME} so that it can be read at runtime via
 * reflection, allowing for dynamic SQL generation or object-relational mapping.
 *
 * <p>The annotation targets {@link ElementType#TYPE}, meaning it can only be applied to classes,
 * interfaces, enums, or annotation types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
    /**
     * The name of the database table that this entity class maps to.
     *
     * <p>This value typically corresponds to the exact name of the table in the database.
     *
     * @return The name of the database table.
     */
    String value();
}
