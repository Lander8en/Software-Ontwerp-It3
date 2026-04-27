package ui.editors;

import java.util.List;

import domain.Column;
import domain.ColumnType;
import ui.controllers.TableController;

/**
 * Provides access to default value information and operations for columns,
 * abstracting the interaction with the TableController.
 * Used by editors to retrieve and update column default values.
 */
public class ColumnDefaultValueAccess {

    private final TableController controller;

    /**
     * Constructs a new ColumnDefaultValueAccess for the given controller.
     *
     * @param controller the TableController associated with the table context
     */
    public ColumnDefaultValueAccess(TableController controller) {
        this.controller = controller;
    }

    /**
     * Returns the default value of the column at the given index.
     *
     * @param colIndex the column index
     * @return the default value string
     */
    public String getValue(int colIndex) {
        return controller.getDefaultValue(colIndex);
    }

    /**
     * Updates the default value of the column at the given index.
     *
     * @param colIndex the column index
     * @param value    the new default value to set
     */
    public void setValue(int colIndex, String value) {
        controller.setDefaultValue(colIndex, value);
    }

    /**
     * Returns all columns currently available in the table.
     *
     * @return a list of Column objects
     */
    public List<Column> getAllItems() {
        return controller.columnsRequest();
    }

    /**
     * Returns the type of the given column.
     *
     * @param column the column whose type is to be retrieved
     * @return the ColumnType of the column
     */
    public ColumnType getType(Column column) {
        return column.getType();
    }

    /**
     * Returns whether the given column allows blank values.
     *
     * @param column the column to check
     * @return true if blanks are allowed, false otherwise
     */
    public boolean allowsBlanks(Column column) {
        return column.isBlanksAllowed();
    }
}