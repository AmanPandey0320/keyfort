package com.kabutar.keyfort.data.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;

import com.kabutar.keyfort.data.annotation.Column;
import com.kabutar.keyfort.data.annotation.Entity;
import com.kabutar.keyfort.data.annotation.Id;
import com.kabutar.keyfort.data.sql.SqlQueryWithFields;
import com.mysql.cj.result.Row;

import reactor.core.publisher.Mono;

/**
 * An abstract base class providing common functionality for database repositories.
 * <p>
 * This class includes a logger for logging repository-related activities and
 * provides a utility method {@link #getCreateSQL(Class)} for generating SQL
 * `CREATE TABLE` statements based on entity class annotations.
 * </p>
 * <p>
 * Subclasses are required to implement the {@link #create()} method, which
 * typically handles the creation of the underlying database schema or resources
 * for the repository.
 * </p>
 */
public abstract class BaseRepository<T,C> {

    /**
     * The logger instance for this repository, initialized using Log4j2.
     * This logger is used for recording information, warnings, and errors
     * related to repository operations.
     */
    private final Logger logger = LogManager.getLogger(BaseRepository.class);
    private final Class<?> entityClass;
    private final DatabaseClient dbClient;
    
    /**
     * @consuctor
     * @param clazz
     */
    protected BaseRepository(Class<C> clazz,DatabaseClient dbClient) {
        this.entityClass = clazz;
        this.dbClient = dbClient;
    }

    /**
     * Abstract method to be implemented by subclasses for creating necessary
     * database structures or resources.
     * <p>
     * This method is expected to perform asynchronous operations, returning
     * a {@link Mono} that completes when the creation process is finished.
     * </p>
     *
     * @return A {@link Mono<Void>} that signals completion when the creation
     * operation is done.
     * @throws Exception If an error occurs during the creation process.
     */
    public abstract void create() throws Exception;
    
    /**
     * Abstract method to be implemented by subclasses for inserting data in
     * tables
     * <p>
     * This method is expected to perform asynchronous operations, returning
     * the id of type T that completes when the creation process is finished.
     * </p>
     *
     * @return T (id)
     * @throws Exception If an error occurs during the insertion process.
     */
    public abstract Mono<T> save() throws Exception;

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
    protected String getCreateSQL() throws Exception {
    	Class<?> clazz = entityClass;
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
        return sb.toString();
    }
    
    /**
     * Creates a database table based on the provided class definition.
     *
     * This method constructs a SQL CREATE TABLE statement from the given class
     * (presumably by inspecting its annotations or structure to derive table name
     * and column definitions) and executes it using the provided `DatabaseClient`.
     * It logs the SQL statement, the success or failure of the table creation,
     * and handles any errors during the process.
     *
     * @param clazz The {@link Class} object representing the entity for which the
     * database table needs to be created. This class is used to derive
     * the table's schema.
     * @param dbClient The {@link DatabaseClient} instance used to interact with the
     * database and execute the generated SQL statement.
     * @return A {@link reactor.core.publisher.Mono Mono<Void>} that completes
     * successfully if the table is created, or emits an error if the
     * creation fails. The `Mono<Void>` signifies that no data is
     * returned upon successful completion, only the completion signal.
     * @throws Exception If an error occurs during the generation of the CREATE TABLE
     * SQL statement from the provided class. Note that database
     * execution errors are handled within the Mono's error
     * handling chain.
     */
    protected void createTable(DatabaseClient dbClient) throws Exception {
    	String CREATE_TABLE_SQL = this.getCreateSQL();

        logger.info("Attempting to create table with SQL: {} for class: {}", CREATE_TABLE_SQL,entityClass.getName());

        dbClient.sql(CREATE_TABLE_SQL).fetch().rowsUpdated().doOnSuccess(rows -> {
            
            logger.info("Created table {} (rowsUpdated: {})",entityClass.getName(), rows);
        })
        .doOnError(e -> {
            
            logger.error("Error creating table {}, reason: {}", entityClass.getName(), e.getMessage());
            logger.debug("Full exception: ", e); // Log full stack trace for debug
             throw new RuntimeException("Failed to create table", e);
        })
        .then().block();
    };
    
    /**
     * 
     * @param object
     * @return
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	protected Mono<T> insertIntoTable(C object) throws Exception {
    	SqlQueryWithFields sql = this.getInsertSQL(object);
    	DatabaseClient.GenericExecuteSpec spec = this.dbClient.sql(sql.getSql());
    	System.out.println(sql.getSql());
    	System.out.println(sql.getFields().size());
    	// Reflection-driven binding
        for (int i = 0; i < sql.getFields().size(); i++) {
            Field f = sql.getFields().get(i);
            f.setAccessible(true);
            Object value = f.get(object);
            if(value == null) {
            	spec = spec.bindNull(f.getName(),f.getType());
            }else {
            	spec = spec.bind(f.getName(), value);
            }
            
        }
        

        // Fetch auto-generated primary key
        return spec.fetch()
                .first()
                .map(row -> (T) row.get("id"));
        
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
    protected SqlQueryWithFields getInsertSQL(Object entity) throws Exception {
    	logger.debug("Entering getInsertSQL function");
        Class<?> clazz = entity.getClass();

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
    
}
