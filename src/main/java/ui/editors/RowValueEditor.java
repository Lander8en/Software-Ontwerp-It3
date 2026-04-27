package ui.editors;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import domain.ColumnType;

/**
 * Handles user editing of individual cell values in a table's row.
 * Supports validation, drawing, and keyboard input.
 */
public class RowValueEditor {

    private int editingRowIndex = -1;
    private int editingColIndex = -1;
    private String currentInput = "";
    private String originalName = "";
    private boolean editing = false;

    private final RowValueAccess access;

    /**
     * Constructs a RowValueEditor using the given access helper.
     *
     * @param access the access interface for row value operations
     */
    public RowValueEditor(RowValueAccess access) {
        this.access = access;
    }

    /**
     * Starts editing the cell at the specified row and column.
     */
    public void startEditing(int rowIndex, int colIndex) {
        this.editingRowIndex = rowIndex;
        this.editingColIndex = colIndex;
        this.originalName = access.getValue(rowIndex, colIndex);
        this.currentInput = originalName != null ? originalName : "";
        this.editing = true;
    }

    /**
     * Continues editing at a new target without resetting the current input.
     */
    public void continueEditing(int rowIdx, int colIdx) {
        this.editingRowIndex = rowIdx;
        this.editingColIndex = colIdx;
        this.editing = true;
    }

    /**
     * Handles key events to update the editor state.
     */
    public void handleKeyEvent(int id, int keyCode, char keyChar) {
        if (!editing) return;

        switch (keyCode) {
            case KeyEvent.VK_ENTER -> commitEdit();
            case KeyEvent.VK_ESCAPE -> cancelEdit();
            case KeyEvent.VK_BACK_SPACE -> {
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                }
            }
            default -> {
                if (!Character.isISOControl(keyChar) && keyChar != KeyEvent.CHAR_UNDEFINED) {
                    currentInput += keyChar;
                }
            }
        }
    }

    /**
     * Draws the cell at the given location, optionally showing a red border.
     */
    public void draw(Graphics g, int rowIndex, int colIndex, int x, int y, int cellWidth,
                     ColumnType type, Boolean redBorder) {
        boolean isTarget = editing && editingRowIndex == rowIndex && editingColIndex == colIndex;
        String value = isTarget ? currentInput : access.getValue(rowIndex, colIndex);

        if (type == ColumnType.BOOLEAN) {
            int boxSize = 14;
            int boxX = x + 2;
            int boxY = y - boxSize;

            g.setColor(Color.white);
            g.fillRect(boxX, boxY, boxSize, boxSize);

            g.setColor(value.isBlank() ? Color.LIGHT_GRAY : Color.BLACK);
            g.drawRect(boxX, boxY, boxSize, boxSize);

            if (redBorder) {
                g.setColor(Color.RED);
                g.drawRect(boxX - 1, boxY - 1, boxSize + 1, boxSize + 1);
            }

            if ("true".equalsIgnoreCase(value.trim())) {
                g.drawLine(boxX + 3, boxY + 7, boxX + 6, boxY + 10);
                g.drawLine(boxX + 6, boxY + 10, boxX + 11, boxY + 4);
            }

        } else {
            g.setColor(Color.WHITE);
            g.fillRect(x - 2, y - 15, cellWidth + 1, 18);

            if (isTarget) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x - 2, y - 15, cellWidth + 1, 18);

                if (!isValid()) {
                    g.setColor(Color.RED);
                    g.drawRect(x - 3, y - 16, cellWidth + 3, 20);
                }
            }

            g.setColor(Color.BLACK);
            g.drawRect(x - 2, y - 15, cellWidth + 1, 18);
            g.drawString(value + (isTarget ? "_" : ""), x, y);

            if (redBorder) {
                g.setColor(Color.RED);
                g.drawRect(x - 3, y - 16, cellWidth + 3, 20);
            }
        }
    }

    /**
     * Commits the current input as the new value for the cell.
     */
    public void commitEdit() {
        if (editingRowIndex != -1 && isValid()) {
            access.setValue(editingRowIndex, editingColIndex, currentInput);
            stopEditing();
        }
    }

    /**
     * Cancels the current edit and restores the original value.
     */
    public void cancelEdit() {
        if (editingRowIndex != -1) {
            access.setValue(editingRowIndex, editingColIndex, originalName);
            stopEditing();
        }
    }

    /**
     * Stops editing and resets the state.
     */
    public void stopEditing() {
        editing = false;
        editingRowIndex = -1;
        editingColIndex = -1;
        currentInput = "";
    }

    public boolean isEditing() {
        return editing;
    }

    public boolean wasEditing(int rowIdx, int colIdx) {
        return (rowIdx == editingRowIndex && editingColIndex == colIdx);
    }

    public int getEditingColIndex() {
        return editingColIndex;
    }

    public int getEditingRowIndex() {
        return editingRowIndex;
    }

    /**
     * Validates the current input against the column's type and blank policy.
     */
    public boolean isValid() {
        if (!editing) return true;

        String value = currentInput.trim();
        ColumnType type = access.getType(editingColIndex);
        boolean blanksAllowed = access.allowsBlanks(editingColIndex);

        return switch (type) {
            case STRING -> !value.isEmpty() || blanksAllowed;
            case EMAIL -> (value.chars().filter(c -> c == '@').count() == 1) || (value.isEmpty() && blanksAllowed);
            case INTEGER -> {
                if (value.isEmpty()) yield blanksAllowed;
                try {
                    int parsed = Integer.parseInt(value);
                    yield Integer.toString(parsed).equals(value);
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
            case BOOLEAN -> value.equals("true") || value.equals("false") || (value.isEmpty() && blanksAllowed);
        };
    }

    /**
     * Stops editing if the changed cell is currently being edited and the value is invalid.
     */
    public void stopEditingIfChanged(int changedRowIndex, int index) {
        if (editingRowIndex != -1) {
            if (editingRowIndex == changedRowIndex && index == editingColIndex && !isValid()) {
                stopEditing();
            }
        }
    }
}