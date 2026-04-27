package ui.interaction;

import java.awt.event.MouseEvent;

import ui.Subwindow;
import ui.SubwindowManager;
import ui.TablesSubwindow;
import ui.controllers.TableController;

/**
 * A {@link MouseInteractionStrategy} that opens a new subwindow when the user
 * double-clicks on a table name in a {@link TablesSubwindow}.
 * <p>
 * If the selected table has no columns, a {@code TableDesignSubwindow} is opened
 * to allow column creation. Otherwise, a {@code TableRowsSubwindow} is opened to
 * view and edit table data.
 * </p>
 */
public class OpenTableStrategy implements MouseInteractionStrategy {

    /**
     * Determines whether this strategy wants to handle the given mouse interaction.
     * <p>
     * This strategy is interested in double-clicks inside the table name area of a
     * {@link TablesSubwindow}, unless editing is blocked (e.g., due to validation).
     * </p>
     *
     * @param window     the subwindow under the cursor
     * @param x          the x-coordinate of the mouse
     * @param y          the y-coordinate of the mouse
     * @param clickCount the number of mouse clicks
     * @return true if the interaction should trigger opening a subwindow
     */
    @Override
    public boolean wantsToHandle(Subwindow window, int x, int y, int clickCount) {
        return (window instanceof TablesSubwindow tableWindow)
                && tableWindow.isInTableNameArea(x, y)
                && !tableWindow.blockEditing()
                && clickCount == 2;
    }

    /**
     * Opens either a design or rows subwindow depending on whether the selected
     * table has any columns.
     *
     * @param manager    the subwindow manager
     * @param window     the window under the mouse
     * @param id         the type of mouse event (must be {@code MOUSE_CLICKED})
     * @param x          the x-coordinate of the mouse
     * @param y          the y-coordinate of the mouse
     * @param clickCount the number of mouse clicks
     */
    @Override
    public void handle(SubwindowManager manager, Subwindow window, int id, int x, int y, int clickCount) {
        if (!(window instanceof TablesSubwindow tableWindow)) return;
        if (id != MouseEvent.MOUSE_CLICKED) return;

        int index = tableWindow.getTableIndexFromY(y);
        if (index < 0 || index >= tableWindow.tablesCount()) return;

        tableWindow.setSelectedTable(index);
        TableController tableController = tableWindow.getTableController(index);

        if (tableController.getColumnsCount() == 0) {
            manager.addNewTableDesignSubwindow(tableController);
        } else {
            manager.addNewTableRowsSubwindow(tableController);
        }
    }
}