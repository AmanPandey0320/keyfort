package com.kabutar.keyfort.data.sql;

import java.lang.reflect.Field;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SqlQueryWithFields {
	private List<Field> fields;
	private String sql;
	
}
