package com.kabutar.keyfort.data.sql;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;
import com.kabutar.keyfort.data.annotation.Id;

public class SqlProcessor {
	private final static Logger logger = LogManager.getLogger(SqlProcessor.class);
	/**
     * Generates an SQL UPDATE statement with named parameters based on entity fields.
     * Updates all fields annotated with @Column, excludes @Id fields from update set.
     * Uses the @Id field as the WHERE condition for the update.
     *
     * Example output for Dimension entity:
     * UPDATE dimensions 
     * SET name = :name, display_name = :displayName, is_active = :isActive
     * WHERE id = :id;
     *
     * @param entity The entity instance to analyze via reflection.
     * @return SqlQueryWithFields containing ordered fields (excluding @Id first, then @Id for where)
     *         and the update SQL string with named parameters.
     * @throws Exception if entity class is not annotated with @Entity or has no @Id field.
     */
    private static SqlQueryWithFields getUpdateSQL(Class<?> clazz) throws Exception {
        logger.debug("Entering getUpdateSQL function");

        if (!clazz.isAnnotationPresent(Entity.class)) {
            logger.debug("The class: {} is not an entity", clazz.getName());
            throw new Exception("The class " + clazz.getName() + " is not an entity");
        }

        String tableName = clazz.getAnnotation(Entity.class).value();

        StringBuilder setClause = new StringBuilder();
        Field idField = null;
        String idColumnName = null;

        List<Field> updateFields = new ArrayList<>();
        
        boolean isFirst = true;

        // Collect fields, separated into update fields and id field
        while (clazz != null) {
            logger.debug("Destructuring fields of entity class: {}", clazz.getName());
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                boolean isId = field.isAnnotationPresent(Id.class);
                boolean isColumn = field.isAnnotationPresent(Column.class);

                if (isId) {
                    if (idField != null) {
                        throw new Exception("Multiple @Id fields are not supported");
                    }
                    idField = field;
                    idColumnName = field.getAnnotation(Id.class).name();
                    continue;
                }

                if (isColumn) {
                    String columnName = field.getAnnotation(Column.class).name();

                    if (!isFirst) {
                        setClause.append(", ");
                    } else {
                        isFirst = false;
                    }

                    setClause.append(columnName).append(" = :").append(field.getName());
                    updateFields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }

        if (idField == null) {
            throw new Exception("No @Id field found in class " + clazz.getName());
        }

        // Append id field last for binding clarity
        updateFields.add(idField);

        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + idColumnName + " = :" + idField.getName() + ";";

        logger.debug("Generated UPDATE SQL: {}", sql);
        logger.debug("Exiting getUpdateSQL function");

        return new SqlQueryWithFields(updateFields, sql);
    }
    
    /**
     * Generates a SQL CREATE TABLE statement for a given entity class.
     * <p>
     * This method inspects the provided {@code Class<?>} for the presence of the {@link Entity} annotation
     * and iterates through its fields (including inherited fields) to find those annotated with {@link Column}.
     * It constructs a `CREATE TABLE IF NOT EXISTS` statement based on the table name specified in the {@link Entity}
     * annotation and the column definitions from the {@link Column} annotations.
     * </p>
     * <p>
     * The method handles fields from superclasses by traversing the class hierarchy until `null` is reached.
     * It ensures proper comma separation between column definitions.
     * </p>
     *
     * @param clazz The {@code Class<?>} object representing the entity for which the SQL CREATE statement is to be generated.
     * This class must be annotated with {@link Entity}.
     * @return A {@code String} containing the SQL `CREATE TABLE IF NOT EXISTS` statement.
     * @throws Exception If the provided `clazz` is not annotated with {@link Entity}.
     * @see Entity
     * @see Column
     */
    private static SqlQueryWithFields getCreateSQL(Class<?> clazz) throws Exception {
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
                    
                    if(column.reference().length() > 0) {
                    	sb.append(",");
                    	sb.append("FOREIGN KEY (")
                    		.append(column.name()).append(") REFERENCES ").append(column.reference());
                    }
                }else if(field.isAnnotationPresent(Id.class)) {
                	Id column = field.getAnnotation(Id.class);
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
        return new SqlQueryWithFields(Collections.emptyList(), sb.toString());
    }
    
    /**
     * Generates an SQL INSERT statement for the given entity object using reflection.
     * <p>
     * The statement excludes the {@code @Id} field (assuming it is database-generated),
     * but still appends {@code RETURNING id} so the generated value can be retrieved.
     * </p>
     *
     * Example output for Dimension:
     * <pre>
     * INSERT INTO dimensions (name, display_name, is_active)
     * VALUES (?, ?, ?)
     * RETURNING id;
     * </pre>
     *
     * @param entity the entity instance (used only to determine class and annotations)
     * @return SQL insert statement with placeholders "?" and "RETURNING id"
     * @throws Exception if the class is not annotated with {@link Entity}
     */
    private static SqlQueryWithFields getInsertSQL(Class<?> clazz) throws Exception {
    	logger.debug("Entering getInsertSQL function");

        if (!clazz.isAnnotationPresent(Entity.class)) {
        	logger.debug("The class: {} is not an entity",clazz.getName());
            throw new Exception("The class " + clazz.getName() + " is not an entity");
        }

        String tableName = clazz.getAnnotation(Entity.class).value();

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Field> insertFields = new ArrayList<>();
        boolean isFirst = true;

        while (clazz != null) {
        	logger.debug("Destructuring fields of entity class: {}",clazz.getName());
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
            	logger.debug("Destructuring field: {} of entity class: {}",field.getName(),clazz.getName());
                boolean isId = field.isAnnotationPresent(Id.class);
                boolean isColumn = field.isAnnotationPresent(Column.class);
                
                // Exclude Id, since it's auto-generated
                if (isId) {
                    continue;
                }

                if (isColumn) {

                    String columnName = field.getAnnotation(Column.class).name();

                    if (!isFirst) {
                        columns.append(", ");
                        values.append(", ");
                    } else {
                        isFirst = false;
                    }

                    columns.append(columnName);
                    values.append(":"+field.getName());
                    insertFields.add(field);
                }
            }
            
            logger.debug("Fnished destructuring entity class: {}",clazz.getName());
            clazz = clazz.getSuperclass();
        }

        
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ") RETURNING id;";
        
        logger.debug("Generated SQL: {}",sql);
        logger.debug("Exiting getInsertSQL function");
        
        return new SqlQueryWithFields(insertFields,sql);
    }

    /**
     * function to generate delete all sql query
     * @param clazz
     * @return
     * @throws Exception
     */
    private static SqlQueryWithFields getDeleteAllSQL(Class<?> clazz) throws Exception {
        logger.debug("Entering getDeleteAllSQL function");

        if (!clazz.isAnnotationPresent(Entity.class)) {
            logger.debug("The class: {} is not an entity", clazz.getName());
            throw new Exception("The class " + clazz.getName() + " is not an entity");
        }

        String tableName = clazz.getAnnotation(Entity.class).value();

        String sql = "DELETE FROM " + tableName + ";";

        logger.debug("Generated SQL: {}", sql);
        logger.debug("Exiting getDeleteAllSQL function");

        // No fields needed for delete all
        return new SqlQueryWithFields(Collections.emptyList(), sql);
    }

    /**
     * Generates a SQL DELETE statement for deleting an entity record by its ID.
     * Inspects the entity class to locate the @Id annotation and to find the table name from @Entity.
     * The generated SQL uses named parameters for the where clause.
     *
     * Example output for Dimension:
     * DELETE FROM dimensions WHERE id = :id;
     *
     * @param clazz The entity class.
     * @return SqlQueryWithFields with list containing the ID field and the SQL string.
     * @throws Exception if @Entity/@Id annotation is missing or multiple @Id fields exist.
     */
    private static SqlQueryWithFields getDeleteByIdSQL(Class<?> clazz) throws Exception {
        logger.debug("Entering getDeleteByIdSQL function");

        if (!clazz.isAnnotationPresent(Entity.class)) {
            logger.debug("The class: {} is not an entity", clazz.getName());
            throw new Exception("The class " + clazz.getName() + " is not an entity");
        }

        String tableName = clazz.getAnnotation(Entity.class).value();

        Field idField = null;
        String idColumnName = null;

        while (clazz != null) {
            logger.debug("Destructuring fields of entity class: {}", clazz.getName());
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                boolean isId = field.isAnnotationPresent(Id.class);
                if (isId) {
                    if (idField != null) {
                        throw new Exception("Multiple @Id fields are not supported");
                    }
                    idField = field;
                    idColumnName = field.getAnnotation(Id.class).name();
                    break; // Only one @Id field supported
                }
            }
            clazz = clazz.getSuperclass();
        }

        if (idField == null) {
            throw new Exception("No @Id field found in class " + clazz.getName());
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + idColumnName + " = :" + idField.getName() + ";";
        logger.debug("Generated DELETE SQL: {}", sql);
        logger.debug("Exiting getDeleteByIdSQL function");

        List<Field> fields = new ArrayList<>();
        fields.add(idField);

        return new SqlQueryWithFields(fields, sql);
    }

    /**
     * returns <K,V> pair of SQL
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Map<String,SqlQueryWithFields> getSqlsForClass(Class<?> clazz) throws Exception {
        Map<String,SqlQueryWithFields> sqlMap = new HashMap<>();

        sqlMap.put(SqlConstant.SqlTypes.CREATE_TABLE_SQL,getCreateSQL(clazz));
        sqlMap.put(SqlConstant.SqlTypes.INSERT_INTO_SQL,getInsertSQL(clazz));
        sqlMap.put(SqlConstant.SqlTypes.UPDATE_TABLE_SQL,getUpdateSQL(clazz));
        sqlMap.put(SqlConstant.SqlTypes.DELETE_ALL_SQL,getDeleteAllSQL(clazz));
        sqlMap.put(SqlConstant.SqlTypes.DELETE_ONE_BY_ID_SQl,getDeleteByIdSQL(clazz));

        return sqlMap;
    }

}
