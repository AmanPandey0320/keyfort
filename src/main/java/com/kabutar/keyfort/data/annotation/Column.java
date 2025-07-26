//package com.kabutar.keyfort.data.entity;
//
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//import java.lang.annotation.ElementType;
//
//// Define annotation to be used on fields
//@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.FIELD) 
//public @interface ColumnName {
//    String value();
//}

package com.kabutar.keyfort.data.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

// Define annotation to be used on fields
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	// Represents the column name
    String name(); 
    
    // Represents a definition or additional detail for the column
    String define(); 
}
