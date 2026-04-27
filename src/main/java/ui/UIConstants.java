package ui;

import java.awt.Color;

public final class UIConstants {

    // Prevent instantiation
    private UIConstants() {}

    // General layout
    public static final int TITLE_BAR_HEIGHT = 30;
    public static final int ROW_HEIGHT = 20;
    public static final int HEADER_HEIGHT = 40;
    public static final int PADDING = 5;

    // Subwindow controls
    public static final int CLOSE_BUTTON_SIZE = 20;
    public static final int NAME_AREA_WIDTH = 200;
    public static final int SPACER = NAME_AREA_WIDTH + 15;

    // Click zones
    public static final int MARGIN_WIDTH = 10;
    public static final int WIDTH_TYPE_CLICK = 100;
    public static final int TYPE_AREA_WIDTH = 285;
    public static final int COLUMN_WIDTH = 200;

    // Default value zone
    public static final int DEFAULT_VALUE_OFFSET = 215 + 150;
    public static final int DEFAULT_VALUE_WIDTH = 100;

    // Scrollbar sizes
    public static final int SCROLLBAR_SIZE = 15;
    public static final int SCROLLBAR_MIN_THUMB_SIZE = 30;

    // Form Subwindow
    public static final int FIELD_HEIGHT = 18;
    public static final int LABEL_WIDTH = 200;
    public static final int FIELD_WIDTH = 200;
    public static final int FIELD_PADDING = 10;
    public static final int FORM_TOP_PADDING = 20;

    // Background
    public static final Color BACKGROUND_COLOR = new Color(145, 165, 180);
}