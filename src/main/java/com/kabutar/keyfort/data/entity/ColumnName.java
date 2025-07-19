package com.kabutar.keyfort.data.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

// Define annotation to be used on fields
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) 
public @interface ColumnName {
    String value();
}
