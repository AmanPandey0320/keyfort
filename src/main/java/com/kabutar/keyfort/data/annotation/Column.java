package com.kabutar.keyfort.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an annotation to mark fields as database columns within an entity.
 *
 * <p>This annotation is applied to fields of classes that represent database tables. It provides
 * metadata necessary for mapping Java fields to SQL table columns, including the column's name and
 * its SQL definition (e.g., data type, constraints).
 *
 * <p>The annotation has {@link RetentionPolicy#RUNTIME} so that it can be read at runtime via
 * reflection, allowing for dynamic SQL generation or object-relational mapping.
 *
 * <p>The annotation targets {@link ElementType#FIELD}, meaning it can only be applied to fields
 * (member variables) of a class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /**
     * Represents the name of the database column.
     *
     * <p>This value typically corresponds to the exact name of the column in the database table.
     *
     * @return The name of the database column.
     */
    String name();

    /**
     * Represents the SQL definition or additional details for the column.
     *
     * <p>This value can include the SQL data type, constraints (e.g., `VARCHAR(255) NOT NULL`, `INT
     * PRIMARY KEY AUTOINCREMENT`, `TEXT DEFAULT ''`). It provides the necessary SQL syntax to
     * define the column when creating a table.
     *
     * @return The SQL definition string for the column.
     */
    String define();

    /**
     * Specifies a reference to a table for the property. This element is optional. If no value is
     * provided, an empty string will be used as the default.
     *
     * @return The reference table details as string. Defaults to an empty string if not specified.
     */
    String reference() default "";
}
