package ui.editors;

import domain.Column;
import domain.ColumnType;
import ui.controllers.TableController;

import java.awt.*;

import static ui.UIConstants.*;

/**
 * Responsible for rendering a single row representing a column in the TableDesignSubwindow.
 * This includes the column name, type, blanks-allowed checkbox, and default value.
 */
public class ColumnRowRenderer {

    private final NameEditor<Column> columnEditor;
    private final ColumnDefaultValueEditor defaultValueEditor;

    /**
     * Constructs a ColumnRowRenderer with the necessary editors and controller.
     *
     * @param columnEditor editor for the column name
     * @param defaultValueEditor editor for the default value
     * @param controller the table controller (currently unused but passed in for consistency)
     */
    public ColumnRowRenderer(NameEditor<Column> columnEditor,
                             ColumnDefaultValueEditor defaultValueEditor,
                             TableController controller) {
        this.columnEditor = columnEditor;
        this.defaultValueEditor = defaultValueEditor;
    }

    /**
     * Draws an individual row representing a column.
     *
     * @param g the Graphics context
     * @param colIndex index of the column
     * @param column the column to render
     * @param rowX x-coordinate of the row
     * @param rowY y-coordinate of the row
     * @param isSelected whether the row is selected
     * @param width total width available for rendering
     */
    public void drawRow(Graphics g, int colIndex, Column column, int rowX, int rowY, boolean isSelected, int width) {
        if (isSelected) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(rowX, rowY, NAME_AREA_WIDTH, ROW_HEIGHT);
        }

        // Draw column name
        columnEditor.drawName(g, colIndex, rowX, rowY, NAME_AREA_WIDTH, ROW_HEIGHT);

        // Draw column type
        g.setColor(Color.DARK_GRAY);
        g.drawString("[" + column.getType().name() + "]", rowX + SPACER, rowY + 15);

        // Highlight type block (if invalid after type change)
        if (defaultValueEditor.isTypeBlocked(colIndex)) {
            g.setColor(Color.RED);
            g.drawRect(rowX + SPACER - 2, rowY, 80, ROW_HEIGHT - 2);
        }

        // Draw "blanks allowed" checkbox
        int checkboxX = rowX + SPACER + 100;
        int checkboxY = rowY + 3;
        int boxSize = 14;

        g.setColor(Color.WHITE);
        g.fillRect(checkboxX, checkboxY, boxSize, boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(checkboxX, checkboxY, boxSize, boxSize);

        if (column.isBlanksAllowed()) {
            g.drawLine(checkboxX + 3, checkboxY + 7, checkboxX + 6, checkboxY + 10);
            g.drawLine(checkboxX + 6, checkboxY + 10, checkboxX + 11, checkboxY + 4);
        }

        // Highlight if blank value is disallowed but current default is blank
        if (isBlankViolation(column)) {
            g.setColor(Color.RED);
            g.drawRect(checkboxX - 1, checkboxY - 1, boxSize + 2, boxSize + 2);
        }

        // Draw default value (text or checkbox depending on type)
        if (defaultValueEditor.isEditing()
                && defaultValueEditor.getEditingTargetIndex() == colIndex
                && column.getType() != ColumnType.BOOLEAN) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(rowX + DEFAULT_VALUE_OFFSET, rowY, width, ROW_HEIGHT);
            defaultValueEditor.drawValue(g, colIndex, column, rowX + DEFAULT_VALUE_OFFSET, rowY, width);
        } else {
            drawDefaultValue(g, column, rowX + DEFAULT_VALUE_OFFSET, rowY, width);
        }
    }

    /**
     * Draws the default value of a column depending on its type.
     */
    private void drawDefaultValue(Graphics g, Column column, int x, int y, int width) {
        String value = column.getDefaultValue();
        ColumnType type = column.getType();

        if (type == ColumnType.BOOLEAN) {
            boolean isBlank = value.isBlank();

            int boxSize = 14;
            int boxX = x + 2;
            int boxY = y + (ROW_HEIGHT - boxSize) / 2;

            if (isBlank) {
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(boxX, boxY, boxSize, boxSize);
            } else {
                g.setColor(Color.BLACK);
                g.drawRect(boxX, boxY, boxSize, boxSize);
                if ("true".equalsIgnoreCase(value.trim())) {
                    g.drawLine(boxX + 3, boxY + 7, boxX + 6, boxY + 10);
                    g.drawLine(boxX + 6, boxY + 10, boxX + 11, boxY + 4);
                }
            }
        } else {
            if (!defaultValueEditor.isValid(column)) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.BLACK);
            }
            g.drawRect(x - 1, y, width, ROW_HEIGHT - 1);
            g.setColor(Color.BLACK);
            g.drawString(value, x + 2, y + 15);
        }
    }

    /**
     * Determines if the current default value violates the blank policy.
     *
     * @param column the column to validate
     * @return true if blanks are not allowed and value is blank
     */
    private boolean isBlankViolation(Column column) {
        return !column.isBlanksAllowed() && column.getDefaultValue().isBlank();
    }
}