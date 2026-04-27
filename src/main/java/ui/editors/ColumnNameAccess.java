package ui.editors;

import java.util.List;
import domain.Column;
import ui.controllers.TableController;

/**
 * Provides access to column names for editing purposes.
 * Implements the {@link NameAccess} interface to abstract name operations
 * on {@link Column} objects via the {@link TableController}.
 */
public class ColumnNameAccess implements NameAccess<Column> {
    
    private final TableController controller;

    /**
     * Constructs a new ColumnNameAccess using the provided table controller.
     *
     * @param controller the controller managing the table's columns
     */
    public ColumnNameAccess(TableController controller) {
        this.controller = controller;
    }

    /**
     * Returns the name of the column at the specified index.
     *
     * @param colIndex the index of the column
     * @return the name of the column
     */
    public String getName(int colIndex) {
        return controller.getColumnName(colIndex);
    }

    /**
     * Sets the name of the column at the specified index.
     *
     * @param colIndex the index of the column
     * @param name the new name to assign
     */
    public void setName(int colIndex, String name) {
        controller.renameColumn(colIndex, name);
    }

    /**
     * Returns all columns managed by the table controller.
     *
     * @return a list of all columns
     */
    public List<Column> getAll() {
        return controller.columnsRequest();
    }
}