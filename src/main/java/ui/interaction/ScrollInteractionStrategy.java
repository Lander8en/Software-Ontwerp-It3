package ui.interaction;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import ui.ScrollablePanel;
import ui.ScrollableWindow;
import ui.Scrollbar;
import ui.Subwindow;
import ui.SubwindowManager;

import static ui.UIConstants.SCROLLBAR_SIZE;

/**
 * A {@link MouseInteractionStrategy} that enables scrolling in a {@link ScrollableWindow}
 * via mouse interaction with the scrollbars.
 *
 * This includes:
 * - Clicking above/below or left/right of the scrollbar thumb to page
 * - Dragging the scrollbar thumb to scroll content
 */
public class ScrollInteractionStrategy implements MouseInteractionStrategy {

    /**
     * Checks whether the mouse event occurred over a scrollbar in a scrollable subwindow.
     *
     * @param window     the target subwindow
     * @param x          mouse x position
     * @param y          mouse y position
     * @param clickCount number of mouse clicks
     * @return true if the event is in a scrollbar zone
     */
    @Override
    public boolean wantsToHandle(Subwindow window, int x, int y, int clickCount) {
        if (!(window instanceof ScrollableWindow)) return false;

        ScrollableWindow scrollWindow = (ScrollableWindow) window;
        Rectangle viewport = scrollWindow.getViewport();

        // Vertical scrollbar bounds
        Rectangle verticalBounds = new Rectangle(
            viewport.x + viewport.width - SCROLLBAR_SIZE,
            viewport.y,
            SCROLLBAR_SIZE,
            viewport.height
        );

        // Horizontal scrollbar bounds
        Rectangle horizontalBounds = new Rectangle(
            viewport.x,
            viewport.y + viewport.height - SCROLLBAR_SIZE,
            viewport.width,
            SCROLLBAR_SIZE
        );

        return scrollWindow.getScrollPanel().getVerticalScrollbar().contains(x, y, verticalBounds)
            || scrollWindow.getScrollPanel().getHorizontalScrollbar().contains(x, y, horizontalBounds);
    }

    /**
     * Handles mouse interactions with scrollbars, including clicking and dragging.
     */
    @Override
    public void handle(SubwindowManager manager, Subwindow window,
                       int id, int x, int y, int clickCount) {

        ScrollableWindow scrollWindow = (ScrollableWindow) window;
        ScrollablePanel scrollPanel = scrollWindow.getScrollPanel();
        Rectangle viewport = scrollWindow.getViewport();

        int relX = x - viewport.x;
        int relY = y - viewport.y;

        Scrollbar vertical = scrollPanel.getVerticalScrollbar();
        Scrollbar horizontal = scrollPanel.getHorizontalScrollbar();

        // Define interaction zones for each scrollbar
        boolean verticalHit = vertical.isEnabled() &&
            vertical.contains(relX, relY, new Rectangle(
                viewport.width - SCROLLBAR_SIZE,
                0,
                SCROLLBAR_SIZE,
                viewport.height - (horizontal.isEnabled() ? SCROLLBAR_SIZE : 0)
            ));

        boolean horizontalHit = horizontal.isEnabled() &&
            horizontal.contains(relX, relY, new Rectangle(
                0,
                viewport.height - SCROLLBAR_SIZE,
                viewport.width - (vertical.isEnabled() ? SCROLLBAR_SIZE : 0),
                SCROLLBAR_SIZE
            ));

        switch (id) {
            case MouseEvent.MOUSE_PRESSED -> {
                if (verticalHit) {
                    int thumbPos = vertical.getThumbPosition();
                    if (relY < thumbPos) {
                        scrollPanel.setScrollY(scrollPanel.getScrollY() - viewport.height);
                    } else if (relY > thumbPos + vertical.getThumbSize()) {
                        scrollPanel.setScrollY(scrollPanel.getScrollY() + viewport.height);
                    } else {
                        vertical.setPressed(true);
                        vertical.setPosition(relY - thumbPos);
                    }
                } else if (horizontalHit) {
                    int thumbPos = horizontal.getThumbPosition();
                    if (relX < thumbPos) {
                        scrollPanel.setScrollX(scrollPanel.getScrollX() - viewport.width);
                    } else if (relX > thumbPos + horizontal.getThumbSize()) {
                        scrollPanel.setScrollX(scrollPanel.getScrollX() + viewport.width);
                    } else {
                        horizontal.setPressed(true);
                        horizontal.setPosition(relX - thumbPos);
                    }
                }
            }

            case MouseEvent.MOUSE_DRAGGED -> {
                if (vertical.isPressed()) {
                    int trackHeight = viewport.height - vertical.getThumbSize();
                    float ratio = (float) (relY - vertical.getPosition()) / trackHeight;
                    int newScrollY = (int) (ratio * (scrollPanel.getContentHeight() - viewport.height));
                    scrollPanel.setScrollY(newScrollY);
                } else if (horizontal.isPressed()) {
                    int trackWidth = viewport.width - horizontal.getThumbSize();
                    float ratio = (float) (relX - horizontal.getPosition()) / trackWidth;
                    int newScrollX = (int) (ratio * (scrollPanel.getContentWidth() - viewport.width));
                    scrollPanel.setScrollX(newScrollX);
                }
            }

            case MouseEvent.MOUSE_RELEASED -> {
                vertical.setPressed(false);
                horizontal.setPressed(false);
            }
        }

        manager.bringToFront(window);
    }
}