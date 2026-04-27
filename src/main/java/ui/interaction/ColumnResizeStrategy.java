package ui.interaction;

import java.awt.event.MouseEvent;
import java.util.List;

import ui.Subwindow;
import ui.SubwindowManager;
import ui.layout.TabularLayout;

/**
 * A mouse interaction strategy that allows resizing of table columns
 * by dragging the right border of their headers.
 *
 * <p>This strategy is intended for subwindows that implement the
 * {@link ResizableColumns} interface, which provides access to
 * column layout information and allows certain columns to be resized.</p>
 *
 * <p><strong>Design notes:</strong><br>
 * – Uses the Strategy Pattern to handle mouse events.<br>
 * – Applies GRASP Controller responsibility to fully manage column resizing.</p>
 */
public class ColumnResizeStrategy implements MouseInteractionStrategy {

    /**
     * Interface for subwindows that support resizable tabular columns.
     * Subwindows implementing this interface enable this resizing strategy.
     */
    public interface ResizableColumns {

        /**
         * @return the x-coordinate of the left edge of the first header cell
         */
        int headerLeft();

        /**
         * Determines if a Y-coordinate is within the header area.
         * 
         * @param y mouse Y-position
         * @return true if within header area, false otherwise
         */
        boolean isInsideHeader(int y);

        /**
         * @return the logical order of the columns in the table
         */
        List<String> columnOrder();

        /**
         * @return the layout object that manages column widths
         */
        TabularLayout layout();

        /**
         * Determines if a given column is resizable.
         *
         * @param column the column identifier
         * @return true if the column can be resized
         */
        default boolean isColumnResizable(String column) {
            return true;
        }
    }

    private ResizableColumns view = null;
    private String column = null;
    private int startX, startWidth;
    private static final int HOT_ZONE = 5;

    /**
     * Checks if the given mouse event should be handled by this strategy.
     * Looks for mouse events close to the right border of a resizable column.
     */
    @Override
    public boolean wantsToHandle(Subwindow w, int x, int y, int clicks) {
        if (!(w instanceof ResizableColumns rc)) return false;
        if (!rc.isInsideHeader(y)) return false;

        int pos = rc.headerLeft();
        for (String c : rc.columnOrder()) {
            int wCol = rc.layout().getWidth(c);
            pos += wCol;

            if (!rc.isColumnResizable(c)) continue;
            if (Math.abs(x - pos) <= HOT_ZONE) return true;
        }
        return false;
    }

    /**
     * Handles column resizing based on mouse interaction events.
     */
    @Override
    public void handle(SubwindowManager mgr, Subwindow w, int id, int x, int y, int clicks) {
        if (!(w instanceof ResizableColumns rc)) return;

        switch (id) {
            case MouseEvent.MOUSE_PRESSED -> beginDrag(rc, x);
            case MouseEvent.MOUSE_DRAGGED -> drag(x);
            case MouseEvent.MOUSE_RELEASED, MouseEvent.MOUSE_EXITED -> endDrag();
        }
    }

    /**
     * Initiates a drag operation if the mouse is near a resizable column border.
     */
    private void beginDrag(ResizableColumns rc, int mouseX) {
        int pos = rc.headerLeft();

        for (String c : rc.columnOrder()) {
            int wCol = rc.layout().getWidth(c);
            pos += wCol;

            if (!rc.isColumnResizable(c)) continue;

            if (Math.abs(mouseX - pos) <= HOT_ZONE) {
                view = rc;
                column = c;
                startX = mouseX;
                startWidth = wCol;
                return;
            }
        }
    }

    /**
     * Continues the drag operation by adjusting the column width.
     */
    private void drag(int mouseX) {
        if (view == null) return;
        int newWidth = startWidth + (mouseX - startX);
        view.layout().setWidth(column, newWidth);
    }

    /**
     * Ends the drag operation and clears internal state.
     */
    private void endDrag() {
        view = null;
        column = null;
    }
}