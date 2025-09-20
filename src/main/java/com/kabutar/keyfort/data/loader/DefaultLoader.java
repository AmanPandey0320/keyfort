package com.kabutar.keyfort.data.loader;

/**
 * Represents a default contract for loading data.
 *
 * <p>Implementations of this interface are expected to provide a concrete mechanism for loading
 * data into a system or component. The specific details of "loading data" (e.g., from a database,
 * file, external service) are left to the implementing classes.
 */
public interface DefaultLoader {
    /**
     * Initiates the data loading process.
     *
     * <p>This method should contain the core logic for fetching, parsing, and processing data to
     * make it available for use. The exact nature of the data and its destination depends on the
     * specific implementation.
     *
     * <p>Implementations should handle any exceptions that may occur during the data loading
     * process, or declare them if they are propagated.
     */
    void loadData();
}
