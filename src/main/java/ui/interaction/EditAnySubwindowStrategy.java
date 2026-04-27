package ui.interaction;

import ui.Subwindow;
import ui.SubwindowManager;

/**
 * Strategy that delegates mouse interactions to any editable {@link Subwindow}.
 * <p>
 * This is a generic fallback strategy that checks if the mouse event occurs
 * in the editable zone of a subwindow. If so, it lets the subwindow handle
 * the event directly.
 *
 * <p>Part of the Strategy pattern used for mouse interaction handling.</p>
 */
public class EditAnySubwindowStrategy implements MouseInteractionStrategy {

    /**
     * Checks whether the mouse event occurred inside the editable area of the subwindow.
     *
     * @param window     the subwindow being interacted with
     * @param x          the x-coordinate of the mouse event
     * @param y          the y-coordinate of the mouse event
     * @param clickCount the number of mouse clicks
     * @return true if the mouse is within the editing zone
     */
    @Override
    public boolean wantsToHandle(Subwindow window, int x, int y, int clickCount) {
        return window.isInEditingZone(x, y);
    }

    /**
     * Delegates the mouse event to the subwindow's own event handler.
     *
     * @param manager    the subwindow manager
     * @param window     the subwindow to handle the event
     * @param id         the mouse event type (e.g., click, drag)
     * @param x          the x-coordinate of the event
     * @param y          the y-coordinate of the event
     * @param clickCount the number of mouse clicks
     */
    @Override
    public void handle(SubwindowManager manager, Subwindow window, int id, int x, int y, int clickCount) {
        window.handleMouseEvent(id, x, y, clickCount);
    }
}