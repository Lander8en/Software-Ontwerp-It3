package ui.layout;

import java.util.*;
import ui.UIConstants;

/**
 * Represents a layout model for a single tabular view.
 * 
 * <p>This class maintains the column widths for a specific table view,
 * allowing consistent rendering across multiple subwindows showing the same table.
 * It uses the column's logical name as a key and supports width updates with clamping
 * and change checks.</p>
 *
 * <p>Design roles:</p>
 * <ul>
 *   <li><b>Information Expert</b> (GRASP): Responsible for knowing column widths.</li>
 *   <li><b>Observer-friendly</b>: Used as a shared layout so all views stay in sync.</li>
 * </ul>
 */
public class TabularLayout {

    // Maintains column widths in logical order of addition
    private final Map<String, Integer> widths = new LinkedHashMap<>();

    /**
     * Returns the current width for the given column name.
     * If the column is not found, a default width is returned.
     *
     * @param column the logical name of the column
     * @return the column width, or a default value if not set
     */
    public int getWidth(String column) {
        return widths.getOrDefault(column, UIConstants.COLUMN_WIDTH);
    }

    /**
     * Sets the width for a given column, clamped to a minimum of 50 pixels.
     * If the new width is the same as the current one, no update is made.
     *
     * @param column the logical name of the column
     * @param w      the new width to set
     */
    public void setWidth(String column, int w) {
        int newWidth = Math.max(50, w); // prevent extremely small or negative widths
        if (newWidth == getWidth(column)) return; // avoid unnecessary updates
        widths.put(column, newWidth);
    }
}