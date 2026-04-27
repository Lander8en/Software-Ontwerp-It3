package domain;

import java.util.ArrayList;
import java.util.List;

import domain.observers.ColumnAttributesObserver;
import domain.observers.RowValueChangeObserver;
import ui.TableDesignSubwindow;

/**
 * Represents a table consisting of columns and rows.
 * Provides access to the table’s data and operations to modify its structure
 * and values.
 */
public class Table {

    private String name;
    private final ColumnRepository columnRepository = new ColumnRepository();
    private final List<Row> rows = new ArrayList<>();
    private final List<RowValueChangeObserver> rowValueObservers = new ArrayList<>();

    // ==== Constructor ====

    /**
     * Constructs a new Table with the given name.
     *
     * @param name the name of the table, must not be null
     * @throws IllegalArgumentException if name is null
     */
    public Table(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Table name cannot be null");
        }
        this.name = name;
    }

    // ==== Getters (queries) ====

    /**
     * Returns a deep copy of this table, including columns and row values.
     */
    public Table getDeepCopy() {
        Table copy = new Table(this.name);

        // Copy columns
        int index = 0;
        for (Column col : columnRepository.getColumns()) {
            copy.addColumn(index, col);
            index++;
        }

        // Copy rows
        for (Row row : this.rows) {
            Row rowCopy = row.getRow(); // Deep copy of the row
            copy.addRow(copy.rows.size(), rowCopy); // Append row at the end
        }

        return copy;
    }

    /**
     * Returns the name of the table.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an empty copy of the table with the same name.
     */
    public Table emptyCopy() {
        return new Table(name);
    }

    /**
     * Returns a deep copy of the list of columns in this table.
     */
    public List<Column> getColumns() {
        return columnRepository.getColumns();
    }

    /**
     * Returns the column at the specified index.
     */
    public Column getColumn(int colIndex) {
        return getColumns().get(colIndex);
    }

    /**
     * Returns the name of the column at the given index.
     */
    public String getColumnName(int colIndex) {
        return columnRepository.getName(colIndex);
    }

    /**
     * Returns a copy of the row at the specified index.
     */
    public Row getRow(int index) {
        return rows.get(index).getRow();
    }

    /**
     * Returns a list of copies of all rows in the table.
     */
    public List<Row> getRows() {
        List<Row> copy = new ArrayList<>();
        for (Row r : rows) {
            copy.add(r.getRow());
        }
        return copy;
    }

    /**
     * Returns the value at the specified row and column.
     */
    public String getValue(int rowIndex, int colIndex) {
        return rows.get(rowIndex).getValue(colIndex);
    }

    /**
     * Returns the number of rows in the table.
     */
    public int getRowsCount() {
        return rows.size();
    }

    /**
     * Returns the type of the column at the given index.
     */
    public ColumnType getType(int colIndex) {
        return columnRepository.getType(colIndex);
    }

    /**
     * Returns the default value of the column at the given index.
     */
    public String getDefaultValue(int columnIndex) {
        return columnRepository.getDefaultValue(columnIndex);
    }

    /**
     * Returns whether blanks are allowed in the column at the given index.
     */
    public boolean isBlanksAllowed(int columnIndex) {
        return columnRepository.isBlanksAllowed(columnIndex);
    }

    /**
     * Returns the index of the specified column.
     */
    public int getColumnIndex(Column column) {
        return columnRepository.indexOf(column);
    }

    /**
     * Returns the number of columns in the table.
     */
    public int getColumnsCount() {
        return columnRepository.getColumnsCount();
    }

    // ==== Modifiers (commands) ====

    /**
     * Changes the name of this table.
     *
     * @param newName the new name to assign; must not be null
     */
    public void setName(String newName) {
        if (newName == null) {
            throw new IllegalArgumentException("Table name cannot be null");
        }
        this.name = newName;
    }

    /**
     * Creates a new row with default values based on current columns.
     */
    public Row createNewRow() {
        List<Column> columns = getColumns();
        List<String> defaults = new ArrayList<>();
        for (Column col : columns) {
            defaults.add(col.getDefaultValue());
        }
        return new Row(columns.size(), defaults);
    }

    /**
     * Adds a row at the given index.
     */
    public void addRow(int index, Row row) {
        rows.add(index, row);
    }

    /**
     * Deletes the row at the specified index.
     */
    public void deleteRow(int index) {
        rows.remove(index);
    }

    /**
     * Changes the type of the column at the given index.
     */
    public void setColumnType(int colIndex, ColumnType type) {
        columnRepository.setType(colIndex, type);
    }

    /**
     * Creates and returns a new column, and appends default values to each row.
     */
    public Column createNewColumn() {
        Column newColumn = columnRepository.createNewColumn();
        for (Row row : rows) {
            row.addValue(newColumn.getDefaultValue());
        }
        return newColumn;
    }

    /**
     * Adds a column at the specified index.
     */
    public void addColumn(int index, Column column) {
        columnRepository.addColumn(index, column);
    }

    /**
     * Renames the column at the specified index.
     */
    public void renameColumn(int colIndex, String name) {
        columnRepository.rename(colIndex, name);
    }

    /**
     * Removes the column at the specified index.
     */
    public void removeColumn(int colIndex) {
        columnRepository.remove(colIndex);
    }

    /**
     * Toggles whether blanks are allowed in the column at the given index.
     */
    public void toggleBlanksAllowed(int index) {
        columnRepository.toggleBlanksAllowed(index);
    }

    /**
     * Sets the default value for the column at the given index.
     */
    public void setDefaultValue(int index, String value) {
        columnRepository.setDefaultValue(index, value);
    }

    /**
     * Sets the value of a cell in the table and notifies observers.
     */
    public void setRowValue(int rowIndex, int colIndex, String value) {
        Row row = rows.get(rowIndex);
        row.setValue(colIndex, value);
        notifyRowValueObservers(rowIndex, colIndex);
    }

    // ==== Observer logic ====

    /**
     * Registers an observer to be notified on column attribute changes.
     */
    public void addObserverToColumnRepo(ColumnAttributesObserver observer) {
        columnRepository.addObserver(observer);
    }

    /**
     * Registers a subwindow as a name-change observer.
     */
    public void addChangeColumnNameObserver(TableDesignSubwindow window) {
        columnRepository.addNameChangeObserver(window);
    }

    /**
     * Registers an observer to be notified when a row value changes.
     */
    public void addRowValueObserver(RowValueChangeObserver observer) {
        rowValueObservers.add(observer);
    }

    /**
     * Removes a row value observer.
     */
    public void removeRowValueObserver(RowValueChangeObserver observer) {
        rowValueObservers.remove(observer);
    }

    /**
     * Notifies row observers that a cell has changed.
     */
    private void notifyRowValueObservers(int rowIndex, int colIndex) {
        for (RowValueChangeObserver observer : rowValueObservers) {
            observer.onRowValueChange(rowIndex, colIndex);
        }
    }

    // ==== Equality overrides ====

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Table other = (Table) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}