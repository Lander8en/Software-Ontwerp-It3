package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import static ui.UIConstants.SCROLLBAR_MIN_THUMB_SIZE;

/**
 * A scroll bar component that can be either vertical or horizontal.
 * 
 * <p>Used to represent the visible scrolling position in a scrollable area.
 * The scrollbar manages its position, whether it's enabled, and the appearance of
 * its "thumb" (the draggable portion of the scrollbar).</p>
 */
public class Scrollbar {

    /** Whether this scrollbar is vertical (true) or horizontal (false). */
    private final boolean vertical;

    /** Logical scroll position (in pixels). */
    private int position;

    /** Position of the scrollbar's thumb (in pixels relative to start of track). */
    private int thumbPosition;

    /** Size of the scrollbar thumb (length along the scrolling direction). */
    private int thumbSize;

    /** Whether this scrollbar is currently active and visible. */
    private boolean enabled;

    /** Whether the thumb is currently being dragged. */
    private boolean pressed;

    /**
     * Constructs a new scrollbar.
     *
     * @param vertical true for a vertical scrollbar; false for horizontal
     */
    public Scrollbar(boolean vertical) {
        this.vertical = vertical;
    }

    /**
     * Updates the scrollbar based on the content and viewport size.
     *
     * @param contentSize  the size of the scrollable content (height or width)
     * @param viewportSize the size of the visible viewport (height or width)
     */
    public void update(int contentSize, int viewportSize) {
        this.enabled = contentSize > viewportSize || position > this.thumbSize/2;
        if (enabled) {
            if(contentSize < viewportSize){
                this.thumbSize = Math.max(SCROLLBAR_MIN_THUMB_SIZE, 
                contentSize);
            }else{
                this.thumbSize = Math.max(SCROLLBAR_MIN_THUMB_SIZE, 
                viewportSize * viewportSize / contentSize);
            }
        }
    }

    /**
     * Sets the scroll position.
     *
     * @param position the scroll position in pixels
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Draws the scrollbar within the given bounds.
     *
     * @param g      the graphics context to draw on
     * @param bounds the bounding rectangle of the scrollbar
     * @param active whether the parent window is currently active
     */
    public void draw(Graphics g, Rectangle bounds, boolean active) {
        if (!enabled) {
            drawDisabled(g, bounds, active);
            return;
        }

        // Draw scrollbar track
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Draw thumb
        g.setColor(Color.GRAY);
        if (vertical) {
            if(bounds.y + thumbPosition + thumbSize > bounds.y + bounds.height){
                int diff = bounds.y + thumbPosition + thumbSize - (bounds.y + bounds.height);
                g.fillRect(bounds.x + 2, bounds.y + thumbPosition - diff, bounds.width - 4, thumbSize);
            }else{
                g.fillRect(bounds.x + 2, bounds.y + thumbPosition, bounds.width - 4, thumbSize);
            }
        } else {
            if(bounds.x + thumbPosition + thumbSize > bounds.x + bounds.width){
                int diff = bounds.x + thumbPosition + thumbSize - (bounds.x + bounds.width);
                g.fillRect(bounds.x + thumbPosition - diff, bounds.y + 2, thumbSize, bounds.height - 3);
            }else{
                g.fillRect(bounds.x + thumbPosition, bounds.y + 2, thumbSize, bounds.height - 3);
            }
        }

        // Draw border
        g.setColor(Color.GRAY);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /**
     * Draws a disabled scrollbar.
     *
     * @param g      the graphics context
     * @param bounds the bounds of the scrollbar
     * @param active whether the parent window is active
     */
    private void drawDisabled(Graphics g, Rectangle bounds, boolean active) {
        g.setColor(active ? new Color(240, 240, 240) : new Color(250, 250, 250));
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /**
     * Returns true if the scrollbar's bounds contain the given mouse coordinates.
     */
    public boolean contains(int x, int y, Rectangle bounds) {
        return bounds.contains(x, y);
    }

    // === Getters ===

    public boolean isVertical() {
        return vertical;
    }

    public int getPosition() {
        return position;
    }

    public int getThumbPosition() {
        return thumbPosition;
    }

    public int getThumbSize() {
        return thumbSize;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isPressed() {
        return pressed;
    }

    // === Setters ===

    public void setThumbPosition(int thumbPosition) {
        this.thumbPosition = thumbPosition;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}