package ui;

import java.awt.Graphics;
import java.awt.Rectangle;

import static ui.UIConstants.SCROLLBAR_SIZE;

/**
 * Represents a scrollable panel used in subwindows to enable vertical and/or horizontal scrolling.
 * Maintains both scroll state and scrollbar visuals, and translates drawing accordingly.
 *
 * <p>This class provides methods to set content and viewport dimensions, manage scrollbar state,
 * and render scrollbars. It also offers utilities for applying and resetting scroll transformations
 * to a {@link Graphics} context.</p>
 */
public class ScrollablePanel {

    private final Scrollbar verticalScrollbar;
    private final Scrollbar horizontalScrollbar;
    private int scrollX;
    private int scrollY;
    private int contentWidth;
    private int contentHeight;
    private int viewportWidth;
    private int viewportHeight;

    /**
     * Constructs a new ScrollablePanel with vertical and horizontal scrollbars.
     * Scrollbars will be shown only when needed.
     */
    public ScrollablePanel() {
        this.verticalScrollbar = new Scrollbar(true);
        this.horizontalScrollbar = new Scrollbar(false);
    }

    /**
     * Sets the size of the visible area (viewport) of the panel.
     *
     * @param width  the viewport width
     * @param height the viewport height
     */
    public void setViewportSize(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        updateScrollbars();
    }

    /**
     * Sets the full size of the scrollable content inside the panel.
     *
     * @param width  the content width
     * @param height the content height
     */
    public void setContentSize(int width, int height) {
        this.contentWidth = width + (verticalScrollbar.isEnabled() ? SCROLLBAR_SIZE : 0);
        this.contentHeight = height + (horizontalScrollbar.isEnabled() ? SCROLLBAR_SIZE : 0);
        updateScrollbars();
    }

    /**
     * Updates scrollbar state and thumb positions based on content and viewport sizes.
     */
    private void updateScrollbars() {
        verticalScrollbar.update(contentHeight, viewportHeight);
        horizontalScrollbar.update(contentWidth, viewportWidth);

        if (verticalScrollbar.isEnabled()) {
            verticalScrollbar.setThumbPosition(
                scrollY * (viewportHeight - verticalScrollbar.getThumbSize()) /
                Math.max(1, contentHeight - viewportHeight)
            );
        }

        if (horizontalScrollbar.isEnabled()) {
            horizontalScrollbar.setThumbPosition(
                scrollX * (viewportWidth - horizontalScrollbar.getThumbSize()) /
                Math.max(1, contentWidth - viewportWidth + (verticalScrollbar.isEnabled() ? SCROLLBAR_SIZE : 0))
            );
        }
    }

    /**
     * Applies scroll transformations to the graphics context so content is drawn relative to scroll position.
     *
     * @param g        the Graphics context
     * @param viewport the visible area to clip drawing to
     */
    public void applyScroll(Graphics g, Rectangle viewport) {
        g.setClip(viewport);
        g.translate(-scrollX, -scrollY);
    }

    /**
     * Resets scroll transformations applied to the graphics context.
     *
     * @param g the Graphics context
     */
    public void resetScroll(Graphics g) {
        g.translate(scrollX, scrollY);
        g.setClip(null);
    }

    /**
     * Draws both scrollbars if needed.
     *
     * @param g        the Graphics context
     * @param viewport the visible area of the panel
     * @param active   whether the containing window is currently active
     */
    public void drawScrollbars(Graphics g, Rectangle viewport, boolean active) {
        if (verticalScrollbar.isEnabled()) {
            Rectangle vScrollBounds = new Rectangle(
                viewport.x + viewport.width - SCROLLBAR_SIZE,
                viewport.y,
                SCROLLBAR_SIZE,
                viewport.height
            );
            verticalScrollbar.draw(g, vScrollBounds, active);
        }

        if (horizontalScrollbar.isEnabled()) {
            Rectangle hScrollBounds = new Rectangle(
                viewport.x,
                viewport.y + viewport.height - SCROLLBAR_SIZE,
                viewport.width - (verticalScrollbar.isEnabled() ? SCROLLBAR_SIZE : 0),
                SCROLLBAR_SIZE
            );
            horizontalScrollbar.draw(g, hScrollBounds, active);
        }
    }

    // === Getters ===

    public Scrollbar getVerticalScrollbar() {
        return verticalScrollbar;
    }

    public Scrollbar getHorizontalScrollbar() {
        return horizontalScrollbar;
    }

    public int getScrollX() {
        return scrollX;
    }

    public int getScrollY() {
        return scrollY;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public int getContentWidth() {
        return contentWidth;
    }

    // === Setters ===

    /**
     * Sets the horizontal scroll offset and updates scrollbar state.
     *
     * @param scrollX the new scroll position along the X axis
     */
    public void setScrollX(int scrollX) {
        this.scrollX = Math.max(0, Math.min(contentWidth - viewportWidth, scrollX));
        updateScrollbars();
    }

    /**
     * Sets the vertical scroll offset and updates scrollbar state.
     *
     * @param scrollY the new scroll position along the Y axis
     */
    public void setScrollY(int scrollY) {
        this.scrollY = Math.max(0, Math.min(contentHeight - viewportHeight, scrollY));
        updateScrollbars();
    }
}