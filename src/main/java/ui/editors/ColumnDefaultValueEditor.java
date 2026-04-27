package ui.editors;

import static ui.UIConstants.ROW_HEIGHT;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;

import domain.Column;
import domain.ColumnType;

/**
 * Editor component for editing the default value of a column in a tabular view.
 * Manages edit state, input validation, and rendering of current/default values.
 */
public class ColumnDefaultValueEditor {

    private int editingTargetIndex = -1;
    private String currentInput = "";
    private boolean editing = false;
    private int blockedTypeColumnIndex = -1;

    private final ColumnDefaultValueAccess valueAccess;

    /**
     * Constructs a new default value editor with the given access to column values.
     */
    public ColumnDefaultValueEditor(ColumnDefaultValueAccess valueAccess) {
        this.valueAccess = valueAccess;
    }

    // ==== Editing lifecycle ====

    /**
     * Starts editing the default value of the column at the given index.
     */
    public void startEditing(int colIndex) {
        this.editingTargetIndex = colIndex;
        this.currentInput = valueAccess.getValue(colIndex);
        this.editing = true;
    }

    /**
     * Continues editing by targeting a different column.
     */
    public void continueEditing(int targetIndex) {
        this.editingTargetIndex = targetIndex;
        this.editing = true;
    }

    /**
     * Handles key input while editing. Includes character input, commit (ENTER), cancel (ESC), and backspace.
     */
    public void handleKeyEvent(int id, int keyCode, char keyChar) {
        if (!editing) return;

        switch (keyCode) {
            case KeyEvent.VK_ENTER -> {
                if (isValid()) commitEdit();
            }
            case KeyEvent.VK_ESCAPE -> stopEditing();
            case KeyEvent.VK_BACK_SPACE -> {
                if (!currentInput.isEmpty()) {
                    currentInput = currentInput.substring(0, currentInput.length() - 1);
                }
            }
            default -> {
                if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@._-".indexOf(keyChar) >= 0) {
                    currentInput += keyChar;
                }
            }
        }
    }

    // ==== Drawing ====

    /**
     * Draws the current default value or user input for a column.
     * Highlights with a red border if the current input is invalid.
     */
    public void drawValue(Graphics g, int colIndex, Column column, int x, int y, int width) {
        boolean isTarget = editing && colIndex == editingTargetIndex;
        String value = isTarget ? currentInput + "_" : valueAccess.getValue(colIndex);

        if (isTarget && !isValid()) {
            g.setColor(Color.RED);
            g.drawRect(x - 1, y, width, ROW_HEIGHT);
        }

        g.setColor(Color.BLACK);
        g.drawString(value, x + 2, y + 15);
    }

    // ==== Commit/cancel logic ====

    /**
     * Commits the current input to the model and stops editing.
     */
    public void commitEdit() {
        if (editingTargetIndex != -1) {
            valueAccess.setValue(editingTargetIndex, currentInput);
            stopEditing();
        }
    }

    /**
     * Cancels editing and clears the current input.
     */
    public void stopEditing() {
        editing = false;
        editingTargetIndex = -1;
        currentInput = "";
    }

    // ==== State access ====

    public boolean isEditing() {
        return editing;
    }

    public int getEditingTargetIndex() {
        return editingTargetIndex;
    }

    // ==== Validation ====

    /**
     * Validates the current input based on column type and blank policy.
     */
    public boolean isValid() {
        if (editingTargetIndex == -1) return true;

        List<Column> columns = valueAccess.getAllItems();
        Column editingTargetCopy = columns.get(editingTargetIndex);

        String value = currentInput.trim();
        ColumnType type = editingTargetCopy.getType();
        boolean blanksAllowed = editingTargetCopy.isBlanksAllowed();

        return switch (type) {
            case BOOLEAN -> true; // Not editable manually
            case STRING -> !value.isEmpty() || (value.isEmpty() && blanksAllowed);
            case EMAIL -> value.chars().filter(c -> c == '@').count() == 1 || (value.isEmpty() && blanksAllowed);
            case INTEGER -> {
                if (value.isEmpty()) yield blanksAllowed;
                try {
                    int parsed = Integer.parseInt(value);
                    yield Integer.toString(parsed).equals(value);
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
        };
    }

    /**
     * Validates the given column's default value (used when redrawing or type is changed).
     */
    public boolean isValid(Column column) {
        List<Column> columns = valueAccess.getAllItems();
        int colIndex = columns.indexOf(column);
        String value = isEditing() && getEditingTargetIndex() == colIndex
            ? currentInput
            : column.getDefaultValue();

        if (value.isBlank()) {
            return column.isBlanksAllowed();
        }

        return switch (column.getType()) {
            case STRING -> true;
            case EMAIL -> value.chars().filter(ch -> ch == '@').count() == 1;
            case INTEGER -> value.matches("(-?[1-9]\\d*|0)");
            case BOOLEAN -> value.equals("true") || value.equals("false");
        };
    }

    // ==== Type blocking logic ====

    /**
     * Checks whether the given column is currently blocked for type changes.
     */
    public boolean isTypeBlocked(int colIndex) {
        return blockedTypeColumnIndex == colIndex;
    }

    /**
     * Marks a column index as blocked (usually due to invalid values for a new type).
     */
    public void setTypeBlocked(int colIndex) {
        blockedTypeColumnIndex = colIndex;
    }

    /**
     * Returns true if any column is currently blocked.
     */
    public boolean isTypeBlocked() {
        return blockedTypeColumnIndex != -1;
    }

    /**
     * Clears the blocked column index.
     */
    public void clearTypeBlock() {
        blockedTypeColumnIndex = -1;
    }

    /**
     * Gets the column index currently blocked for type changes.
     */
    public int getBlockedColumnIndex() {
        return blockedTypeColumnIndex;
    }

    /**
     * Stops editing if the currently edited column has become invalid.
     */
    public void stopEditingIfChanged(int changedItemIndex) {
        if (changedItemIndex == editingTargetIndex && !isValid()) {
            stopEditing();
        }
    }
}