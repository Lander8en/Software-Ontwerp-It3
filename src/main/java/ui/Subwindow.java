package ui;

import java.awt.Graphics;

import static ui.UIConstants.*;

/**
 * Abstract base class for all subwindow types in the UI.
 * 
 * Provides shared behavior for layout, hit detection (e.g. title bar, close button),
 * resizing logic, and interaction zones.
 */
public abstract class Subwindow {

    protected int x, y, width, height;
    protected String title;

    /**
     * Creates a new subwindow with the given position, dimensions and title.
     *
     * @param x      X-coordinate of the top-left corner
     * @param y      Y-coordinate of the top-left corner
     * @param width  Width of the subwindow (must be positive)
     * @param height Height of the subwindow (must be positive)
     * @param title  Title string (must not be null)
     */
    public Subwindow(int x, int y, int width, int height, String title) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Subwindow dimensions must be positive.");
        }
        if (title == null) {
            throw new IllegalArgumentException("Subwindow title cannot be null.");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.title = title;
    }

    // === Position access ===

    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    // === Hit detection ===

    public boolean contains(int mx, int my) {
        return isInside(mx, my, x, y, width, height);
    }

    public boolean isInTitleBar(int mx, int my) {
        return isInside(mx, my, x, y, width, TITLE_BAR_HEIGHT);
    }

    public boolean isInCloseButton(int mx, int my) {
        int closeX = x + width - CLOSE_BUTTON_SIZE - PADDING;
        int closeY = y + PADDING;
        return isInside(mx, my, closeX, closeY, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE);
    }

    public boolean isInEditingZone(int mx, int my) {
        if (this instanceof ScrollableWindow window) {
            boolean hasV = window.getScrollPanel().getVerticalScrollbar().isEnabled();
            boolean hasH = window.getScrollPanel().getHorizontalScrollbar().isEnabled();
            int w = width - (hasV ? SCROLLBAR_SIZE : 0);
            int h = height - TITLE_BAR_HEIGHT - (hasH ? SCROLLBAR_SIZE : 0);
            return isInside(mx, my, x, y + TITLE_BAR_HEIGHT + 1, w, h);
        }
        return isInside(mx, my, x, y + TITLE_BAR_HEIGHT + 1, width, height - TITLE_BAR_HEIGHT);
    }

    public boolean isInSelectionMargin(int mouseX, int rowX) {
        return isInside(mouseX, 0, rowX, 0, MARGIN_WIDTH, Integer.MAX_VALUE);
    }

    public boolean isInScrollBar(int mouseX, int rowX) {
        return isInside(mouseX, 0, rowX, 0, MARGIN_WIDTH, Integer.MAX_VALUE);
    }

    public boolean isOnScrollBar(int mouseX, int rowX) {
        return isInside(mouseX, 0, rowX, 0, MARGIN_WIDTH, Integer.MAX_VALUE);
    }

    /**
     * Returns the Y coordinate of the top of the list/content area.
     */
    public int getListTopY() {
        return y + TITLE_BAR_HEIGHT + PADDING;
    }

    public int getRowHeight() {
        return ROW_HEIGHT;
    }

    /**
     * Returns the resize zone that the mouse position lies in, or {@code NONE} if none.
     */
    public ResizeZone getResizeZone(int mx, int my) {
        int border = 5;
        boolean left   = mx >= x && mx <= x + border;
        boolean right  = mx >= x + width - border && mx <= x + width;
        boolean top    = my >= y && my <= y + border;
        boolean bottom = my >= y + height - border && my <= y + height;

        if (top && left) return ResizeZone.TOP_LEFT;
        if (top && right) return ResizeZone.TOP_RIGHT;
        if (bottom && left) return ResizeZone.BOTTOM_LEFT;
        if (bottom && right) return ResizeZone.BOTTOM_RIGHT;
        if (top) return ResizeZone.TOP;
        if (bottom) return ResizeZone.BOTTOM;
        if (left) return ResizeZone.LEFT;
        if (right) return ResizeZone.RIGHT;
        return ResizeZone.NONE;
    }

    /**
     * Resizes the subwindow according to the specified resize zone and delta.
     *
     * @param zone      the edge or corner being dragged
     * @param dx        the change in X
     * @param dy        the change in Y
     * @param minWidth  minimum allowed width
     * @param minHeight minimum allowed height
     */
    public void applyResize(ResizeZone zone, int dx, int dy, int minWidth, int minHeight) {
        int newX = x, newY = y;
        int newWidth = width, newHeight = height;

        switch (zone) {
            case RIGHT -> newWidth += dx;
            case BOTTOM -> newHeight += dy;
            case LEFT -> { newX += dx; newWidth -= dx; }
            case TOP -> { newY += dy; newHeight -= dy; }
            case TOP_LEFT -> { newX += dx; newWidth -= dx; newY += dy; newHeight -= dy; }
            case TOP_RIGHT -> { newWidth += dx; newY += dy; newHeight -= dy; }
            case BOTTOM_LEFT -> { newX += dx; newWidth -= dx; newHeight += dy; }
            case BOTTOM_RIGHT -> { newWidth += dx; newHeight += dy; }
            case NONE -> {}
        }

        if (newWidth >= minWidth && newHeight >= minHeight) {
            this.x = newX;
            this.y = newY;
            this.width = newWidth;
            this.height = newHeight;
        }
    }

    /**
     * Helper method to check whether a point (mx, my) lies inside a rectangle.
     */
    protected boolean isInside(int mx, int my, int rx, int ry, int rWidth, int rHeight) {
        return mx >= rx && mx <= rx + rWidth && my >= ry && my <= ry + rHeight;
    }

    /**
     * Renders this subwindow.
     *
     * @param g        the graphics context
     * @param isActive true if the window is focused
     */
    public abstract void draw(Graphics g, boolean isActive);

    /**
     * Handles a key event sent to this subwindow.
     */
    public abstract void handleKeyEvent(int id, int keyCode, char keyChar);

    /**
     * Handles a mouse event sent to this subwindow.
     */
    public abstract void handleMouseEvent(int id, int x, int y, int clickCount);

    /**
     * Returns the name of the associated table.
     */
    public abstract String getTableName();

    /**
     * Enumeration of all valid resize zones around the border of a subwindow.
     */
    public enum ResizeZone {
        NONE, LEFT, RIGHT, TOP, BOTTOM, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
}