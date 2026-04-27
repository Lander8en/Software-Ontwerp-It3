package domain;

import java.util.ArrayList;
import java.util.List;

import domain.naming.NameGenerator;
import domain.naming.SequentialNamingStrategy;
import domain.observers.ColumnNameChangeObserver;
import domain.observers.ColumnAttributesObserver;

/**
 * Manages a collection of columns in a table.
 * Provides methods to query and modify columns, and to notify observers of changes.
 */
public class ColumnRepository {

    private final List<Column> columns = new ArrayList<>();
    private final List<ColumnAttributesObserver> observers = new ArrayList<>();
    private final List<ColumnNameChangeObserver> nameChangeObservers = new ArrayList<>();
    private final NameGenerator nameGenerator = new NameGenerator(new SequentialNamingStrategy());

    // ==== Observer registration ====

    /**
     * Adds an observer to be notified when column attributes change.
     *
     * @param observer the observer to register; must not be null
     * @throws IllegalArgumentException if observer is null
     */
    public void addObserver(ColumnAttributesObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer must not be null");
        }
        observers.add(observer);
    }

    /**
     * Removes a previously registered column attributes observer.
     *
     * @param observer the observer to remove; must not be null
     * @throws IllegalArgumentException if observer is null
     */
    public void removeObserver(ColumnAttributesObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer must not be null");
        }
        observers.remove(observer);
    }

    /**
     * Adds an observer to be notified when column names change.
     *
     * @param observer the observer to register
     */
    public void addNameChangeObserver(ColumnNameChangeObserver observer) {
        nameChangeObservers.add(observer);
    }

    /**
     * Removes a previously registered name change observer.
     *
     * @param observer the observer to remove
     */
    public void removeNameChangeObserver(ColumnNameChangeObserver observer) {
        nameChangeObservers.remove(observer);
    }

    // ==== Column access ====

    /**
     * Returns a deep copy of all columns in this repository.
     *
     * @return a new list of cloned columns
     */
    public List<Column> getColumns() {
        List<Column> copy = new ArrayList<>();
        for (Column col : columns) {
            Column colCopy = new Column(col.getName());
            colCopy.setType(col.getType());
            colCopy.setBlanksAllowed(col.isBlanksAllowed());
            colCopy.setDefaultValue(col.getDefaultValue());
            copy.add(colCopy);
        }
        return copy;
    }

    /**
     * Returns the name of the column at the given index.
     *
     * @param colIndex index of the column
     * @return column name
     */
    public String getName(int colIndex) {
        return columns.get(colIndex).getName();
    }

    /**
     * Returns the type of the column at the given index.
     */
    public ColumnType getType(int colIndex) {
        return columns.get(colIndex).getType();
    }

    /**
     * Returns whether the column at the given index allows blanks.
     */
    public boolean allowsBlanks(int colIndex) {
        return columns.get(colIndex).isBlanksAllowed();
    }

    /**
     * Returns the default value of the column at the given index.
     */
    public String getDefaultValue(int columnIndex) {
        return columns.get(columnIndex).getDefaultValue();
    }

    /**
     * Returns whether blanks are allowed in the column at the given index.
     */
    public boolean isBlanksAllowed(int columnIndex) {
        return columns.get(columnIndex).isBlanksAllowed();
    }

    /**
     * Returns the index of the given column in the repository.
     *
     * @param column the column to look up
     * @return the index, or -1 if not found
     */
    public int indexOf(Column column) {
        return columns.indexOf(column);
    }

    /**
     * Returns the number of columns.
     */
    public int getColumnsCount() {
        return columns.size();
    }

    // ==== Column modification ====

    /**
     * Creates a new column with a unique name.
     *
     * @return the new column
     */
    public Column createNewColumn() {
        String name = nameGenerator.generateUniqueName("Column", columns);
        return new Column(name);
    }

    /**
     * Adds a column at the given index.
     *
     * @param index  the index to insert at
     * @param column the column to add
     */
    public void addColumn(int index, Column column) {
        columns.add(index, column);
    }

    /**
     * Renames the column at the given index and notifies name observers.
     *
     * @param colIndex index of the column
     * @param name     new name to assign
     */
    public void rename(int colIndex, String name) {
        columns.get(colIndex).setName(name);
        notifyNameChangeObservers(colIndex);
    }

    /**
     * Removes the column at the given index and notifies name observers.
     *
     * @param colIndex index of the column
     */
    public void remove(int colIndex) {
        columns.remove(colIndex);
        notifyNameChangeObservers(colIndex);
    }

    /**
     * Sets a new type for the column at the given index and notifies observers.
     */
    public void setType(int colIndex, ColumnType newType) {
        columns.get(colIndex).setType(newType);
        notifyObservers(colIndex);
    }

    /**
     * Toggles whether blanks are allowed for the column at the given index.
     */
    public void toggleBlanksAllowed(int index) {
        Column column = columns.get(index);
        column.setBlanksAllowed(!column.isBlanksAllowed());
        notifyObservers(index);
    }

    /**
     * Updates the default value of the column at the given index and notifies observers.
     */
    public void setDefaultValue(int index, String string) {
        Column column = columns.get(index);
        column.setDefaultValue(string);
        notifyObservers(index);
        notifyObserversChangeDefault(index);
    }

    // ==== Private observer notification methods ====

    private void notifyObservers(int changedColumnIndex) {
        for (ColumnAttributesObserver observer : observers) {
            observer.onColumnChanged(changedColumnIndex);
        }
    }

    private void notifyObserversChangeDefault(int changedColumnIndex) {
        for (ColumnAttributesObserver observer : observers) {
            observer.onDefaultValueChanged(changedColumnIndex);
        }
    }

    private void notifyNameChangeObservers(int colIndex) {
        for (ColumnNameChangeObserver observer : nameChangeObservers) {
            observer.onColumnNameChange(colIndex);
        }
    }
}