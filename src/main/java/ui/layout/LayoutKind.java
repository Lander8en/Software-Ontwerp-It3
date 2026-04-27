package ui.layout;

/**
 * Represents the type of layout used in a subwindow.
 *
 * <p>This enum helps distinguish between different views that use tabular layouts,
 * such as the design view for editing columns, and the rows view for editing data.</p>
 */
public enum LayoutKind {
    /**
     * Layout used for the table design view (columns).
     */
    TABLE_DESIGN,

    /**
     * Layout used for the table rows view (data).
     */
    TABLE_ROWS
}