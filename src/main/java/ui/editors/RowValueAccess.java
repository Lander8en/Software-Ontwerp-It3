package ui.editors;

import domain.ColumnType;
import ui.controllers.TableController;

/**
 * A helper class that provides access to row values and column metadata
 * through the TableController. Used by editors for value retrieval and modification.
 */
public class RowValueAccess {

    private final TableController controller;

    /**
     * Constructs a RowValueAccess tied to a specific TableController.
     *
     * @param controller the controller managing the table's data
     */
    public RowValueAccess(TableController controller) {
        this.controller = controller;
    }

    /**
     * Returns the string value at the given row and column.
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     * @return the string value at the given cell
     */
    public String getValue(int rowIndex, int columnIndex) {
        return controller.getValue(rowIndex, columnIndex);
    }

    /**
     * Sets the string value at the specified row and column.
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     * @param value       the value to set
     */
    public void setValue(int rowIndex, int columnIndex, String value) {
        controller.setRowValue(rowIndex, columnIndex, value);
    }

    /**
     * Returns the data type of the column at the given index.
     *
     * @param colIndex the column index
     * @return the column type
     */
    public ColumnType getType(int colIndex) {
        return controller.typeRequest(colIndex);
    }

    /**
     * Returns whether blank values are allowed in the specified column.
     *
     * @param colIndex the column index
     * @return true if blanks are allowed; false otherwise
     */
    public boolean allowsBlanks(int colIndex) {
        return controller.columnAllowsBlanks(colIndex);
    }
}