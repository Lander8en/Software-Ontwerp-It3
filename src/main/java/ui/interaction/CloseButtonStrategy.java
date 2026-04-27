package ui.interaction;

import ui.Subwindow;
import ui.SubwindowManager;

import java.awt.event.MouseEvent;

/**
 * A mouse interaction strategy that closes a {@link Subwindow}
 * when the close button is clicked.
 *
 * <p>This strategy is triggered when the user clicks inside the close button area
 * of a subwindow, and performs the close operation through the {@link SubwindowManager}.</p>
 */
public class CloseButtonStrategy implements MouseInteractionStrategy {

    /**
     * Determines whether this strategy wants to handle the event.
     * This happens if the click is inside the close button area of the subwindow.
     *
     * @param w          the subwindow to inspect
     * @param x          the x-coordinate of the mouse event
     * @param y          the y-coordinate of the mouse event
     * @param clickCount the number of mouse clicks
     * @return true if the event is within the close button area
     */
    @Override
    public boolean wantsToHandle(Subwindow w, int x, int y, int clickCount) {
        return w != null && w.isInCloseButton(x, y);
    }

    /**
     * Handles the mouse click to close the window.
     * Only performs the close operation on a mouse click event.
     *
     * @param manager    the subwindow manager responsible for window lifecycle
     * @param w          the target subwindow
     * @param id         the type of mouse event
     * @param x          the x-coordinate of the event
     * @param y          the y-coordinate of the event
     * @param clickCount the number of mouse clicks
     */
    @Override
    public void handle(SubwindowManager manager, Subwindow w, int id, int x, int y, int clickCount) {
        if (manager == null || w == null) return;

        if (id == MouseEvent.MOUSE_CLICKED) {
            manager.closeSubwindow(w);
        }
    }
}