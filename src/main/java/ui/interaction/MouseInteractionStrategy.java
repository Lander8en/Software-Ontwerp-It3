package ui.interaction;

import ui.Subwindow;
import ui.SubwindowManager;

/**
 * Interface for implementing different mouse interaction strategies for subwindows.
 * <p>
 * Concrete strategies determine whether they want to handle a given mouse event
 * and define the behavior for handling it.
 *
 * <p>This is part of the Strategy pattern used in {@link MouseInteractionDispatcher}
 * to delegate mouse interactions in a flexible and modular way.</p>
 */
public interface MouseInteractionStrategy {

    /**
     * Determines whether this strategy wants to handle a mouse event.
     *
     * @param window     the subwindow that received the event
     * @param x          the x-coordinate of the mouse event
     * @param y          the y-coordinate of the mouse event
     * @param clickCount the number of clicks (e.g., single vs double click)
     * @return true if this strategy wants to handle the event; false otherwise
     */
    boolean wantsToHandle(Subwindow window, int x, int y, int clickCount);

    /**
     * Handles the mouse event with custom logic.
     *
     * @param manager    the subwindow manager responsible for managing subwindows
     * @param window     the subwindow involved in the interaction
     * @param id         the type of mouse event (e.g., MOUSE_PRESSED)
     * @param x          the x-coordinate of the mouse event
     * @param y          the y-coordinate of the mouse event
     * @param clickCount the number of mouse clicks
     */
    void handle(SubwindowManager manager, Subwindow window, int id, int x, int y, int clickCount);
}