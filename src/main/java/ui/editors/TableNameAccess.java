package ui.editors;

import java.util.List;
import domain.Table;
import ui.controllers.TableRepoController;

/**
 * Provides access to table names for use in name editors.
 * Implements the {@link NameAccess} interface for working with {@link Table} objects.
 */
public class TableNameAccess implements NameAccess<Table> {

    private final TableRepoController controller;

    /**
     * Constructs a TableNameAccess using the given table repository controller.
     *
     * @param controller the controller to delegate table name operations to
     */
    public TableNameAccess(TableRepoController controller) {
        this.controller = controller;
    }

    /**
     * Returns the name of the table at the given index.
     *
     * @param index the index of the table
     * @return the table name
     */
    @Override
    public String getName(int index) {
        return controller.getTableName(index);
    }

    /**
     * Sets a new name for the table at the given index.
     *
     * @param index the index of the table to rename
     * @param name the new name to assign
     */
    @Override
    public void setName(int index, String name) {
        controller.rename(index, name);
    }

    /**
     * Returns a list of all tables managed by the controller.
     *
     * @return a list of Table objects
     */
    @Override
    public List<Table> getAll() {
        return controller.tablesRequest();
    }
}