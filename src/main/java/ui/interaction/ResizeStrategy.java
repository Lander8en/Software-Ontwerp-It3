package ui.interaction;

import java.awt.event.MouseEvent;
import ui.Subwindow;
import ui.Subwindow.ResizeZone;
import ui.SubwindowManager;

/**
 * A {@link MouseInteractionStrategy} that allows the user to resize a {@link Subwindow}
 * by dragging its borders or corners.
 *
 * <p>This class supports resizing from any valid resize zone (edges or corners).
 * Minimum width and height constraints are enforced during resizing.</p>
 */
public class ResizeStrategy implements MouseInteractionStrategy {

    private Subwindow resizingSubwindow = null;
    private ResizeZone resizeZone = ResizeZone.NONE;
    private int resizeStartX, resizeStartY;

    private static final int MIN_WIDTH = 100;
    private static final int MIN_HEIGHT = 100;

    /**
     * Determines whether this strategy wants to handle the mouse event based on
     * whether the cursor is in a resizable area (border or corner).
     *
     * @param w          the subwindow under the mouse
     * @param x          the x-coordinate of the mouse
     * @param y          the y-coordinate of the mouse
     * @param clickCount the number of mouse clicks
     * @return true if the mouse is over a resizable zone
     */
    @Override
    public boolean wantsToHandle(Subwindow w, int x, int y, int clickCount) {
        return w.getResizeZone(x, y) != ResizeZone.NONE;
    }

    /**
     * Handles mouse press, drag, and release events to resize a subwindow.
     *
     * @param manager    the subwindow manager
     * @param window     the subwindow under the mouse
     * @param id         the mouse event ID
     * @param x          the x-coordinate of the mouse
     * @param y          the y-coordinate of the mouse
     * @param clickCount the number of mouse clicks
     */
    @Override
    public void handle(SubwindowManager manager, Subwindow window, int id, int x, int y, int clickCount) {
        switch (id) {
            case MouseEvent.MOUSE_PRESSED -> {
                resizeZone = window.getResizeZone(x, y);
                if (resizeZone != ResizeZone.NONE) {
                    resizingSubwindow = window;
                    resizeStartX = x;
                    resizeStartY = y;
                    manager.bringToFront(window);
                }
            }

            case MouseEvent.MOUSE_DRAGGED -> {
                if (resizingSubwindow != null && resizeZone != ResizeZone.NONE) {
                    int dx = x - resizeStartX;
                    int dy = y - resizeStartY;

                    resizingSubwindow.applyResize(resizeZone, dx, dy, MIN_WIDTH, MIN_HEIGHT);

                    resizeStartX = x;
                    resizeStartY = y;
                }
            }

            case MouseEvent.MOUSE_RELEASED -> {
                resizingSubwindow = null;
                resizeZone = ResizeZone.NONE;
            }
        }
    }
}