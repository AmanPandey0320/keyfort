package com.kabutar.keyfort.data.repository;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;

import reactor.core.publisher.Mono;

public abstract class BaseRepository {
	private final Logger logger = LogManager.getLogger(BaseRepository.class);
	public abstract Mono<Void> create() throws Exception;
	
//  String CREATE_DIMENSIONS_TABLE_SQL =
//  "CREATE TABLE IF NOT EXISTS dimensions (" +
//  "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
//  "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
//  "created_by VARCHAR(255)," +
//  "updated_by VARCHAR(255)," +
//  "id VARCHAR(255) PRIMARY KEY," +
//  "name VARCHAR(255) NOT NULL UNIQUE," +
//  "display_name VARCHAR(255)," +
//  "is_active BOOLEAN NOT NULL DEFAULT TRUE" +
//  ");";
	
	protected String getCreateSQL(Class<?> clazz) throws Exception {
		if(!clazz.isAnnotationPresent(Entity.class)) {
			throw new Exception("The class " + clazz.getName() + " is not an entity");
		}
		String tableName = clazz.getAnnotation(Entity.class).value();
		
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		
		sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
		
		while(clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			
			for(Field field:fields) {
				if(field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					if(!isFirst) {
						sb.append((","));
					}else {
						isFirst = false;
					}
					sb.append(column.name()).append(" ").append(column.define());
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		sb.append(");");
		return sb.toString();
	}
}
