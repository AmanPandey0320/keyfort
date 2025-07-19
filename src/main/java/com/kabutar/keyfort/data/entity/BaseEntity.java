package com.kabutar.keyfort.data.entity;

import java.lang.reflect.Field;

import io.r2dbc.spi.Row;

public abstract class BaseEntity {
	protected void digest(Row row,Class<?> clazz,BaseEntity object) {
		Field[] fields = clazz.getDeclaredFields();
		for(Field field:fields) {
			if(field.isAnnotationPresent(ColumnName.class)) {
				ColumnName column = field.getAnnotation(ColumnName.class);
				field.setAccessible(true);
				try {
					field.set(object, row.get(column.value(),field.getType()));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				field.setAccessible(false);
				
			}
		}
	}
}
