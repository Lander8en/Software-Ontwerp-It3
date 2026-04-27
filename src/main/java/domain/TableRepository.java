package domain;

import java.util.ArrayList;
import java.util.List;

import domain.naming.NameGenerator;
import domain.naming.SequentialNamingStrategy;
import domain.observers.TableNameChangeObserver;
import domain.observers.TableRemovalObserver;

/**
 * Repository for managing multiple tables.
 * Provides functionality to create, retrieve, rename, and remove tables,
 * and notifies observers about table removals or name changes.
 */
public class TableRepository {

    private final List<Table> tables = new ArrayList<>();
    private final NameGenerator nameGenerator = new NameGenerator(new SequentialNamingStrategy());

    private final List<TableRemovalObserver> removalObservers = new ArrayList<>();
    private final List<TableNameChangeObserver> changeObservers = new ArrayList<>();

    // ==== Observer Registration ====

    /**
     * Registers an observer to be notified when a table is removed.
     *
     * @param observer the observer to register; must not be null
     * @throws IllegalArgumentException if observer is null
     */
    public void addRemovalObserver(TableRemovalObserver observer) {
        if (observer == null) throw new IllegalArgumentException("Observer must not be null");
        removalObservers.add(observer);
    }

    /**
     * Registers an observer to be notified when a table name changes.
     *
     * @param observer the observer to register; must not be null
     * @throws IllegalArgumentException if observer is null
     */
    public void addNameChangeObserver(TableNameChangeObserver observer) {
        if (observer == null) throw new IllegalArgumentException("Observer must not be null");
        changeObservers.add(observer);
    }

    /**
     * Unregisters a previously registered table removal observer.
     */
    public void removeRemovalObserver(TableRemovalObserver observer) {
        removalObservers.remove(observer);
    }

    /**
     * Unregisters a previously registered table name change observer.
     */
    public void removeChangeObserver(TableNameChangeObserver observer) {
        changeObservers.remove(observer);
    }

    // ==== Observer Notification ====

    private void notifyRemovalObservers(Table table) {
        for (TableRemovalObserver o : removalObservers) {
            o.onTableRemoved(table);
        }
    }

    private void notifyNameChangeObservers(Table table) {
        for (TableNameChangeObserver o : changeObservers) {
            o.onTableNameChanged(table);
        }
    }

    // ==== Getters and Queries ====

    /**
     * Returns a deep copy of all tables in this repository.
     */
    public List<Table> getTables() {
        List<Table> deepCopy = new ArrayList<>();
        for (Table t : tables) {
            deepCopy.add(t.getDeepCopy());
        }
        return deepCopy;
    }

    /**
     * Returns the index of the given table int this repository.
     */
    public int getTableIndex(Table table) {
        return tables.indexOf(table);
    }

    /**
     * Returns the number of tables in the repository.
     */
    public int getTablesCount() {
        return tables.size();
    }

    /**
     * Returns the name of the table at the given index.
     */
    public String getTableName(int index) {
        return tables.get(index).getName();
    }

    /**
     * Returns a deep copy of the table at the specified index.
     */
    public Table getDeepCopyTable(int tableIndex) {
        return tables.get(tableIndex).getDeepCopy();
    }

    /**
     * Returns the index of the given table, or -1 if not found.
     */
    public int indexOf(Table table) {
        return tables.indexOf(table);
    }

    /**
     * Checks whether a given table name is valid and unique,
     * excluding the specified table from the check.
     */
    public boolean isTableNameValid(String name, Table excludeTable) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        return tables.stream()
            .filter(t -> t != excludeTable)
            .noneMatch(t -> t.getName().equals(name));
    }

    // ==== Modifiers ====

    /**
     * Creates a new table with a unique name. The table is not automatically added to the repository.
     *
     * @return the new table
     */
    public Table createNewTable() {
        String name = nameGenerator.generateUniqueName("Table", tables);
        return new Table(name);
    }

    /**
     * Removes the specified table from the repository and notifies observers.
     *
     * @param table the table to remove; must not be null
     */
    public void remove(Table table) {
        if (table == null) return;
        notifyRemovalObservers(table);
        tables.removeIf(t -> t.equals(table));
    }

    /**
     * Renames the table at the given index, if the new name is valid.
     *
     * @param index   the index of the table to rename
     * @param newName the new name to assign
     */
    public void updateTableName(int index, String newName) {
        Table table = tables.get(index);
        if (!isTableNameValid(newName, table)) return;
        table.setName(newName);
        notifyNameChangeObservers(table.getDeepCopy());
    }

    /**
     * Inserts the specified table into the repository at the given index.
     *
     * @param tableIndex the position to insert the table
     * @param table      the table to add; must not be null
     */
    public void addTable(int tableIndex, Table table) {
        if (table != null) {
            tables.add(tableIndex, table);
        }
    }
}