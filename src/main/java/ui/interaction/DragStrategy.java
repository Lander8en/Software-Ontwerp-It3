package ui.interaction;

import java.awt.event.MouseEvent;
import ui.Subwindow;
import ui.SubwindowManager;

/**
 * A strategy that allows the user to drag a {@link Subwindow} by its title bar.
 *
 * <p>This class is part of the Strategy pattern implementation for mouse interactions.
 * It handles press, drag, and release mouse events to move windows around.</p>
 */
public class DragStrategy implements MouseInteractionStrategy {

    private Subwindow draggingSubwindow = null;
    private int dragOffsetX, dragOffsetY;

    /**
     * Determines whether this strategy should handle the given mouse event.
     * This strategy handles dragging if the click occurs in the window's title bar.
     *
     * @param window     the subwindow receiving the event
     * @param x          the x-coordinate of the mouse event
     * @param y          the y-coordinate of the mouse event
     * @param clickCount the number of mouse clicks
     * @return true if the event is within the title bar of the window
     */
    @Override
    public boolean wantsToHandle(Subwindow window, int x, int y, int clickCount) {
        return window != null && window.isInTitleBar(x, y);
    }

    /**
     * Handles dragging behavior for a {@link Subwindow}.
     * On press, captures the offset; on drag, updates position; on release, resets state.
     *
     * @param manager    the subwindow manager
     * @param window     the subwindow receiving the event
     * @param id         the type of mouse event
     * @param x          the x-coordinate of the mouse
     * @param y          the y-coordinate of the mouse
     * @param clickCount the number of mouse clicks
     */
    @Override
    public void handle(SubwindowManager manager, Subwindow window, int id, int x, int y, int clickCount) {
        if (window == null || manager == null) return;

        switch (id) {
            case MouseEvent.MOUSE_PRESSED -> {
                draggingSubwindow = window;
                dragOffsetX = x - window.getX();
                dragOffsetY = y - window.getY();
                manager.bringToFront(window);
            }

            case MouseEvent.MOUSE_DRAGGED -> {
                if (draggingSubwindow != null) {
                    int newX = x - dragOffsetX;
                    int newY = y - dragOffsetY;
                    draggingSubwindow.setPosition(newX, newY);
                }
            }

            case MouseEvent.MOUSE_RELEASED -> {
                draggingSubwindow = null;
            }
        }
    }
}