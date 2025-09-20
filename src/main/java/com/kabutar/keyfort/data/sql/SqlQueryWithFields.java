package com.kabutar.keyfort.data.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.List;

@Getter
@AllArgsConstructor
public class SqlQueryWithFields {
    private List<Field> fields;
    private String sql;
}
