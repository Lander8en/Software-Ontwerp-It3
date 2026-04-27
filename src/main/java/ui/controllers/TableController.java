package ui.controllers;

import java.util.List;

import domain.Column;
import domain.ColumnType;
import domain.ColumnTypeValidator;
import domain.Table;
import domain.observers.ColumnAttributesObserver;
import domain.observers.RowValueChangeObserver;
import domain.undoLogic.AddColumnCommand;
import domain.undoLogic.AddRowCommand;
import domain.undoLogic.BlanksAllowedCommand;
import domain.undoLogic.ChangeRowValueCommand;
import domain.undoLogic.ColumnDefaultValueCommand;
import domain.undoLogic.DeleteColumnCommand;
import domain.undoLogic.DeleteRowCommand;
import domain.undoLogic.RenameColumnCommand;
import domain.undoLogic.SetColumnTypeCommand;
import domain.undoLogic.UndoManager;
import ui.TableDesignSubwindow;

/**
 * Controller for managing operations on a single Table.
 * Provides methods to manipulate rows, columns, and metadata of the table.
 */
public class TableController {
    
    private final Table table;

    /**
     * Constructs a TableController for the specified table.
     * 
     * @param table the table to control
     */
    public TableController(Table table) {
        this.table = table;
    }

    // ===== ROW OPERATIONS =====

    /**
     * Registers an observer for value changes in the table rows.
     */
    public void addRowValueChangeObserver(RowValueChangeObserver observer) {
        table.addRowValueObserver(observer);
    }

    /**
     * Handles the request to create and add a new row to the table.
     */
    public void handleCreateNewRowRequest() {
        AddRowCommand cmd = new AddRowCommand(table);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Handles the request to delete a row at the given index.
     */
    public void handleDeleteRowRequest(int index) {
        DeleteRowCommand cmd = new DeleteRowCommand(table, index);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Returns the number of rows in the table.
     */
    public int getRowsCount() {
        return table.getRowsCount();
    }

    /**
     * Returns the value at the given row and column index.
     */
    public String getValue(int rowIndex, int colIndex) {
        return table.getValue(rowIndex, colIndex);
    }

    /**
     * Sets a new value at the given row and column index.
     */
    public void setRowValue(int rowIndex, int colIndex, String value) {
        ChangeRowValueCommand cmd = new ChangeRowValueCommand(table, rowIndex, colIndex, value);
        UndoManager.getInstance().execute(cmd);
    }

    // ===== COLUMN OPERATIONS =====

    /**
     * Registers an observer for changes to column attributes.
     */
    public void addObserverToColumnRepo(ColumnAttributesObserver observer) {
        table.addObserverToColumnRepo(observer);
    }

    /**
     * Registers an observer for column name changes.
     */
    public void addChangeColumnNameObserver(TableDesignSubwindow window) {
        table.addChangeColumnNameObserver(window);
    }

    /**
     * Handles the request to add a new column to the table.
     */
    public void handleCreateNewColumnRequest() {
        AddColumnCommand cmd = new AddColumnCommand(table);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Handles the request to delete a column at the given index.
     */
    public void handleDeleteColumnRequest(int colIndex) {
        DeleteColumnCommand cmd = new DeleteColumnCommand(table, colIndex);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Returns the number of columns in the table.
     */
    public int getColumnsCount() {
        return table.getColumnsCount();
    }

    /**
     * Returns a copy of all columns in the table.
     */
    public List<Column> columnsRequest() {
        return table.getColumns();
    }

    /**
     * Returns the column object at the specified index.
     */
    public Column getColumn(int colIndex) {
        return table.getColumn(colIndex);
    }

    /**
     * Returns the index of a given column in the table.
     */
    public int getColumnIndex(Column column) {
        return table.getColumnIndex(column);
    }

    /**
     * Returns the name of the column at the given index.
     */
    public String getColumnName(int colIndex) {
        return table.getColumnName(colIndex);
    }

    /**
     * Returns the type of the column at the given index.
     */
    public ColumnType getColumnType(int colIndex) {
        return table.getType(colIndex);
    }

    /**
     * Handles renaming a column at the specified index.
     */
    public void renameColumn(int colIndex, String name) {
        RenameColumnCommand cmd = new RenameColumnCommand(table, colIndex, name);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Changes the type of the column at the given index.
     */
    public void setColumnType(int colIndex, ColumnType type) {
        SetColumnTypeCommand cmd = new SetColumnTypeCommand(table, colIndex, type);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Returns the current type of the column at the index.
     */
    public ColumnType typeRequest(int colIndex) {
        return table.getType(colIndex);
    }

    /**
     * Returns whether the current type of the column is compatible with its values.
     */
    public boolean isColumnTypeValid(int colIndex) {
        Column column = table.getColumn(colIndex);
        return ColumnTypeValidator.isColumnTypeValid(column, table);
    }

    /**
     * Toggles the blanks-allowed setting for the specified column.
     */
    public void toggleBlanksAllowed(int index) {
        BlanksAllowedCommand cmd = new BlanksAllowedCommand(table, index);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Returns true if blanks are allowed in the specified column.
     */
    public boolean columnAllowsBlanks(int colIndex) {
        return table.isBlanksAllowed(colIndex);
    }

    public boolean isBlanksAllowed(int columnIndex) {
        return table.isBlanksAllowed(columnIndex);
    }

    /**
     * Updates the default value of the column at the given index.
     */
    public void setDefaultValue(int index, String value) {
        ColumnDefaultValueCommand cmd = new ColumnDefaultValueCommand(table, index, value);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Returns the default value of the column at the given index.
     */
    public String getDefaultValue(int columnIndex) {
        return table.getDefaultValue(columnIndex);
    }

    // ===== TABLE METADATA =====

    /**
     * Returns the name of the table controlled by this controller.
     */
    public String getTableName() {
        return table.getName();
    }
}