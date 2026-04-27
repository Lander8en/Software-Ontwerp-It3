package ui;

import java.awt.Rectangle;

/**
 * Interface for subwindows that support scrolling.
 * 
 * <p>Subwindows implementing this interface must provide access to their
 * {@link ScrollablePanel} instance and a {@link Rectangle} representing
 * the visible viewport area.</p>
 *
 * <p>This is typically used by interaction strategies (e.g., scrolling or dragging)
 * to access scroll state and dimensions for redrawing.</p>
 */
public interface ScrollableWindow {

    /**
     * Returns the scrollable panel responsible for managing scroll state and scrollbars.
     *
     * @return the scrollable panel of this window
     */
    ScrollablePanel getScrollPanel();

    /**
     * Returns the rectangle representing the visible (viewport) area of the scrollable content.
     *
     * @return the current viewport
     */
    Rectangle getViewport();
}