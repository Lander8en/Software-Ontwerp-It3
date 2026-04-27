package ui.layout;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository that provides shared {@link TabularLayout} instances for each table view.
 *
 * <p>This class is implemented as a Singleton (using enum-based approach) and uses the Creator pattern
 * to construct and cache layouts associated with specific tables and view types (design or rows).</p>
 *
 * <p>Each table can have a separate layout for its design view and its rows view, ensuring that
 * column width changes in one view are reflected across all subwindows showing that same view.</p>
 */
public enum LayoutRepository {
    INSTANCE;

    /**
     * Composite key to uniquely identify a layout based on view kind and table name.
     */
    private record Key(LayoutKind kind, String tableName) {}

    private final Map<Key, TabularLayout> cache = new ConcurrentHashMap<>();

    /**
     * Returns the singleton instance of the repository.
     */
    public static LayoutRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the shared layout for the design view of the specified table.
     *
     * @param tableName the name of the table
     * @return a shared {@link TabularLayout} for the design view
     */
    public TabularLayout forDesign(String tableName) {
        return layout(LayoutKind.TABLE_DESIGN, tableName);
    }

    /**
     * Returns the shared layout for the rows view of the specified table.
     *
     * @param tableName the name of the table
     * @return a shared {@link TabularLayout} for the rows view
     */
    public TabularLayout forRows(String tableName) {
        return layout(LayoutKind.TABLE_ROWS, tableName);
    }

    /**
     * Internal method to retrieve or create a layout for the given kind and table name.
     *
     * @param kind the layout kind (design or rows)
     * @param tableName the name of the table
     * @return a cached or newly created {@link TabularLayout}
     */
    private TabularLayout layout(LayoutKind kind, String tableName) {
        return cache.computeIfAbsent(new Key(kind, tableName), key -> new TabularLayout());
    }
}