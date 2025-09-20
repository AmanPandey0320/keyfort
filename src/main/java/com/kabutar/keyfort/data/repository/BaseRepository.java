package com.kabutar.keyfort.data.repository;

import com.kabutar.keyfort.data.annotation.Id;
import com.kabutar.keyfort.data.sql.SqlConstant;
import com.kabutar.keyfort.data.sql.SqlProcessor;
import com.kabutar.keyfort.data.sql.SqlQueryWithFields;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.r2dbc.core.DatabaseClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

/**
 * An abstract base class providing common functionality for database repositories.
 *
 * <p>This class includes a logger for logging repository-related activities and provides a utility
 * method {@link #getCreateSQL(Class)} for generating SQL `CREATE TABLE` statements based on entity
 * class annotations.
 *
 * <p>Subclasses are required to implement the {@link #create()} method, which typically handles the
 * creation of the underlying database schema or resources for the repository.
 */
public abstract class BaseRepository<T, C> {

    /**
     * The logger instance for this repository, initialized using Log4j2. This logger is used for
     * recording information, warnings, and errors related to repository operations.
     */
    private final Logger logger = LogManager.getLogger(BaseRepository.class);

    private final Class<C> entityClass;
    private final DatabaseClient dbClient;
    private Map<String, SqlQueryWithFields> sqlMap;

    /**
     * @consuctor
     * @param clazz
     */
    protected BaseRepository(Class<C> clazz, DatabaseClient dbClient) {
        this.entityClass = clazz;
        this.dbClient = dbClient;
        try {
            this.sqlMap = SqlProcessor.getSqlsForClass(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Abstract method to be implemented by subclasses for creating necessary database structures or
     * resources.
     *
     * <p>This method is expected to perform asynchronous operations, returning a {@link Mono} that
     * completes when the creation process is finished.
     *
     * @return A {@link Mono<Void>} that signals completion when the creation operation is done.
     * @throws Exception If an error occurs during the creation process.
     */
    public abstract void create() throws Exception;

    /**
     * Abstract method to be implemented by subclasses for inserting data in tables
     *
     * <p>This method is expected to perform asynchronous operations, returning the id of type T
     * that completes when the creation process is finished.
     *
     * @return T (id)
     * @throws Exception If an error occurs during the insertion process.
     */
    public abstract Mono<?> save(C c) throws Exception; // TODO: change Mono return type

    /**
     * Creates a database table based on the provided class definition.
     *
     * <p>This method constructs a SQL CREATE TABLE statement from the given class (presumably by
     * inspecting its annotations or structure to derive table name and column definitions) and
     * executes it using the provided `DatabaseClient`. It logs the SQL statement, the success or
     * failure of the table creation, and handles any errors during the process.
     *
     * @param clazz The {@link Class} object representing the entity for which the database table
     *     needs to be created. This class is used to derive the table's schema.
     * @param dbClient The {@link DatabaseClient} instance used to interact with the database and
     *     execute the generated SQL statement.
     * @return A {@link reactor.core.publisher.Mono Mono<Void>} that completes successfully if the
     *     table is created, or emits an error if the creation fails. The `Mono<Void>` signifies
     *     that no data is returned upon successful completion, only the completion signal.
     * @throws Exception If an error occurs during the generation of the CREATE TABLE SQL statement
     *     from the provided class. Note that database execution errors are handled within the
     *     Mono's error handling chain.
     */
    protected void createTable(DatabaseClient dbClient) throws Exception {
        String CREATE_TABLE_SQL =
                this.sqlMap.get(SqlConstant.SqlTypes.CREATE_TABLE_SQL).getSql();

        logger.info(
                "Attempting to create table with SQL: {} for class: {}",
                CREATE_TABLE_SQL,
                entityClass.getName());

        dbClient.sql(CREATE_TABLE_SQL)
                .fetch()
                .rowsUpdated()
                .doOnSuccess(rows -> {
                    logger.info("Created table {} (rowsUpdated: {})", entityClass.getName(), rows);
                })
                .doOnError(e -> {
                    logger.error(
                            "Error creating table {}, reason: {}",
                            entityClass.getName(),
                            e.getMessage());
                    logger.debug("Full exception: ", e); // Log full stack trace for debug
                    throw new RuntimeException("Failed to create table", e);
                })
                .then()
                .block();
    }
    ;

    /**
     * @param object
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected Mono<T> insertIntoTable(C object) throws Exception {
        SqlQueryWithFields sql = this.sqlMap.get(SqlConstant.SqlTypes.INSERT_INTO_SQL);
        DatabaseClient.GenericExecuteSpec spec = this.dbClient.sql(sql.getSql());
        System.out.println(sql.getSql());
        System.out.println(sql.getFields().size());
        // Reflection-driven binding
        for (int i = 0; i < sql.getFields().size(); i++) {
            Field f = sql.getFields().get(i);
            f.setAccessible(true);
            Object value = f.get(object);
            if (value == null) {
                spec = spec.bindNull(f.getName(), f.getType());
            } else {
                spec = spec.bind(f.getName(), value);
            }
        }

        // Fetch auto-generated primary key
        return spec.fetch().first().map(row -> (T) row.get("id"));
    }

    /**
     * @param object
     * @return
     * @throws Exception
     */
    protected Mono<String> updateTable(C object) throws Exception {
        SqlQueryWithFields sql = this.sqlMap.get(SqlConstant.SqlTypes.UPDATE_TABLE_SQL);
        DatabaseClient.GenericExecuteSpec spec = this.dbClient.sql(sql.getSql());
        System.out.println(sql.getSql());
        System.out.println(sql.getFields().size());

        // Reflection-driven binding
        for (int i = 0; i < sql.getFields().size(); i++) {
            Field f = sql.getFields().get(i);
            f.setAccessible(true);
            Object value = f.get(object);
            logger.info("Binding field: {} to value {}", f.getName(), value);

            if (f.isAnnotationPresent(Id.class)) {
                // Special handling for UUIDs
                if (value instanceof String) {
                    // Convert the String value to a UUID object
                    UUID uuid = UUID.fromString((String) value);
                    spec = spec.bind(f.getName(), uuid);
                } else {
                    // Handle cases where the ID is already a UUID object
                    spec = spec.bind(f.getName(), value);
                }
            } else if (value == null) {
                // Handle null values for other fields
                spec = spec.bindNull(f.getName(), f.getType());
            } else {
                // Bind non-null values for other fields
                spec = spec.bind(f.getName(), value);
            }
        }

        return spec.fetch().rowsUpdated().map(rowsUpdated -> String.valueOf(rowsUpdated));
    }

    /**
     * function to get one from the db based on sql spec and typecaste to Type C class entity object
     * and return a Mono
     *
     * @param spec
     * @param clazz
     * @return
     */
    protected Mono<C> getOne(DatabaseClient.GenericExecuteSpec spec, Class<C> clazz) {
        return spec.fetch().first().flatMap(row -> {
            try {
                // If constructor expects a Row or similar mapping object
                Constructor<C> ctor = clazz.getDeclaredConstructor(row.getClass());
                return Mono.just(ctor.newInstance(row));
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "No constructor found in " + clazz.getName() + " that takes a "
                                + row.getClass(),
                        e);
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
            }
        });
    }

    /**
     * to get multiple rows from select query
     *
     * @param spec
     * @param clazz
     * @return
     */
    protected Flux<C> getAll(DatabaseClient.GenericExecuteSpec spec, Class<C> clazz) {
        return spec.fetch().all().flatMap(row -> {
            try {
                Constructor<C> ctor = clazz.getDeclaredConstructor(row.getClass());
                return Mono.just(ctor.newInstance(row));
            } catch (NoSuchMethodException e) {
                return Mono.error(new IllegalStateException(
                        "No constructor found in " + clazz.getName() + " that takes a "
                                + row.getClass(),
                        e));
            } catch (Exception e) {
                return Mono.error(
                        new RuntimeException("Failed to instantiate " + clazz.getName(), e));
            }
        });
    }

    /**
     * repo function to delete all
     *
     * @return
     * @throws Exception
     */
    public Mono<Long> deleteAll() throws Exception {
        SqlQueryWithFields sqlQueryWithFields =
                this.sqlMap.get(SqlConstant.SqlTypes.DELETE_ALL_SQL);
        DatabaseClient.GenericExecuteSpec spec = this.dbClient.sql(sqlQueryWithFields.getSql());
        return spec.fetch().rowsUpdated();
    }

    /**
     * repo function to delete by id
     *
     * @param id
     * @return
     */
    public Mono<Long> deleteById(UUID id) {
        SqlQueryWithFields sqlQueryWithFields =
                this.sqlMap.get(SqlConstant.SqlTypes.DELETE_ONE_BY_ID_SQl);
        Field field = sqlQueryWithFields.getFields().getFirst();
        DatabaseClient.GenericExecuteSpec spec =
                this.dbClient.sql(sqlQueryWithFields.getSql()).bind(field.getName(), id);
        return spec.fetch().rowsUpdated();
    }

    /**
     * repo function to get by id
     *
     * @param id
     * @return
     */
    public Mono<C> getById(T id) {
        SqlQueryWithFields sqlQueryWithFields =
                this.sqlMap.get(SqlConstant.SqlTypes.SELECT_FROM_TABLE_SQL);
        Field field = sqlQueryWithFields.getFields().getFirst();
        DatabaseClient.GenericExecuteSpec spec =
                this.dbClient.sql(sqlQueryWithFields.getSql()).bind(field.getName(), id);
        return this.getOne(spec, this.entityClass);
    }

    /** @return */
    public Flux<C> getAll() {
        String sql =
                this.sqlMap.get(SqlConstant.SqlTypes.SELECT_ALL_FROM_TABLE_SQL).getSql();
        DatabaseClient.GenericExecuteSpec spec = this.dbClient.sql(sql);
        return this.getAll(spec, this.entityClass);
    }
}
