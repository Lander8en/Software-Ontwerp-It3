package ui;

import ui.controllers.TableController;
import ui.controllers.TableRepoController;

/**
 * Factory class responsible for creating all types of subwindows in a consistent manner.
 * 
 * <p>This class centralizes subwindow creation and applies a cascading offset to avoid overlapping
 * windows. Each window is offset slightly based on how many were previously created.</p>
 */
public class SubwindowFactory {

    private int createdCount = 0;

    public SubwindowFactory() {
    }

    /**
     * Creates a new {@link TablesSubwindow} for managing the table repository.
     *
     * @param controller the table repository controller
     * @return a new instance of {@code TablesSubwindow}
     */
    public TablesSubwindow createNewTableSubwindow(TableRepoController controller) {
        return createTableSubwindow(controller);
    }

    /**
     * Creates a new {@link TableDesignSubwindow} to design columns of a specific table.
     *
     * @param controller the table controller
     * @return a new instance of {@code TableDesignSubwindow}
     */
    public TableDesignSubwindow createNewTableDesignSubwindow(TableController controller) {
        return createTableDesignSubwindow(controller);
    }

    /**
     * Creates a new {@link TableRowsSubwindow} to display rows of a specific table.
     *
     * @param controller the table controller
     * @return a new instance of {@code TableRowsSubwindow}
     */
    public TableRowsSubwindow createNewTableRowsSubwindow(TableController controller) {
        return createRowSubwindow(controller);
    }

    /**
     * Creates a new {@link FormSubwindow} to display and edit a single row from the table.
     *
     * @param controller the table controller
     * @return a new instance of {@code FormSubwindow}
     */
    public FormSubwindow createNewFormSubwindow(TableController controller) {
        return createFormSubwindow(controller);
    }

    // ---- Internal creation logic ---- //

    /**
     * Internal method to create a {@link TablesSubwindow}.
     */
    private TablesSubwindow createTableSubwindow(TableRepoController controller) {
        int offset = createdCount * 20;
        createdCount++;

        int x = 50 + offset;
        int y = 50 + offset;
        int width = 300;
        int height = 200;

        return new TablesSubwindow(x, y, width, height, "Tables", controller);
    }

    /**
     * Internal method to create a {@link TableDesignSubwindow}.
     */
    private TableDesignSubwindow createTableDesignSubwindow(TableController controller) {
        int offset = createdCount * 20;
        createdCount++;

        int x = 50 + offset;
        int y = 50 + offset;
        int width = 610;
        int height = 200;

        return new TableDesignSubwindow(x, y, width, height, "Table Design - " + controller.getTableName(), controller);
    }

    /**
     * Internal method to create a {@link TableRowsSubwindow}.
     */
    private TableRowsSubwindow createRowSubwindow(TableController controller) {
        int offset = createdCount * 20;
        createdCount++;

        int x = 50 + offset;
        int y = 50 + offset;
        int width = 610;
        int height = 200;

        return new TableRowsSubwindow(x, y, width, height, "Table Rows - " + controller.getTableName(), controller);
    }

    /**
     * Internal method to create a {@link FormSubwindow}.
     */
    private FormSubwindow createFormSubwindow(TableController controller) {
        int offset = createdCount * 20;
        createdCount++;

        int x = 50 + offset;
        int y = 50 + offset;
        int width = 350;
        int height = 200;

        return new FormSubwindow(x, y, width, height, controller.getTableName() + " - Row 1", controller);
    }
}