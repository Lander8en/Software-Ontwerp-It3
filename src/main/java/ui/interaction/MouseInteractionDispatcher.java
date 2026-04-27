package ui.interaction;

import ui.Subwindow;
import ui.SubwindowManager;

import java.util.List;

/**
 * Handles delegation of mouse events to the appropriate {@link MouseInteractionStrategy}.
 * <p>
 * This class loops through all registered strategies and delegates the event
 * to the first one that indicates interest via {@code wantsToHandle}.
 *
 * <p>Part of the Strategy pattern implementation for handling diverse mouse interactions
 * like dragging, resizing, editing, etc.</p>
 */
public class MouseInteractionDispatcher {

    private final List<MouseInteractionStrategy> strategies;

    /**
     * Constructs a dispatcher with a predefined set of mouse interaction strategies.
     * The order of the strategies matters: the first matching strategy handles the event.
     */
    public MouseInteractionDispatcher() {
        this.strategies = List.of(
            new ColumnResizeStrategy(),
            new CloseButtonStrategy(),
            new ResizeStrategy(),
            new DragStrategy(),
            new OpenTableStrategy(),
            new EditAnySubwindowStrategy(),
            new ScrollInteractionStrategy()
        );
    }

    /**
     * Delegates the mouse event to the first matching strategy.
     *
     * @param manager the subwindow manager coordinating all subwindows
     * @param target  the subwindow under the mouse event
     * @param id      the type of mouse event (e.g., pressed, released)
     * @param x       the x-coordinate of the event
     * @param y       the y-coordinate of the event
     * @param clicks  the number of mouse clicks (e.g., 2 for double-click)
     */
    public void dispatchMouseEvent(SubwindowManager manager, Subwindow target,
                                    int id, int x, int y, int clicks) {
        for (MouseInteractionStrategy strategy : strategies) {
            if (strategy.wantsToHandle(target, x, y, clicks)) {
                strategy.handle(manager, target, id, x, y, clicks);
                return;
            }
        }
    }
}