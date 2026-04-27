package ui.editors;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * A reusable component that handles in-place name editing for a list of named items.
 * It manages the input state, validation, and rendering of editable name fields.
 *
 * @param <T> the type of items whose names are being edited
 */
public class NameEditor<T> {

    private int editingTargetIndex = -1;
    private String currentInput = "";
    private String originalName = "";
    private boolean editing = false;

    private final NameAccess<T> access;

    /**
     * Constructs a NameEditor for items managed by the given NameAccess interface.
     *
     * @param access the access strategy to get/set names
     */
    public NameEditor(NameAccess<T> access) {
        this.access = access;
    }

    /**
     * Returns the index of the item currently being edited.
     */
    public int getEditingTargetIndex() {
        return editingTargetIndex;
    }

    /**
     * Starts editing the item at the given index.
     *
     * @param targetIndex the index of the item to edit
     */
    public void startEditing(int targetIndex) {
        this.editingTargetIndex = targetIndex;
        this.originalName = access.getName(targetIndex);
        this.currentInput = originalName;
        this.editing = true;
    }

    /**
     * Continues editing without resetting input.
     *
     * @param targetIndex the index to continue editing
     */
    public void continueEditing(int targetIndex) {
        this.editingTargetIndex = targetIndex;
        this.editing = true;
    }

    /**
     * Handles a key event for editing input.
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
     * Draws the name at the given position.
     */
    public void drawName(Graphics g, int targetIndex, int x, int y, int width, int height) {
        boolean isTarget = editing && targetIndex == editingTargetIndex;
        boolean isValid = !isTarget || isValid();
        String text = isTarget ? currentInput + "_" : access.getName(targetIndex);

        if (isTarget && !isValid) {
            g.setColor(Color.RED);
            g.drawRect(x, y, width, height);
        }

        g.setColor(Color.BLACK);
        g.drawString(text, x + 10, y + 15);
    }

    /**
     * Applies the current input as the new name if valid, and ends editing.
     */
    public void commitEdit() {
        if (editingTargetIndex != -1 && isValid()) {
            access.setName(editingTargetIndex, currentInput);
            stopEditing();
        }
    }

    /**
     * Cancels editing and restores the original name.
     */
    public void cancelEdit() {
        if (editingTargetIndex != -1) {
            access.setName(editingTargetIndex, originalName);
            stopEditing();
        }
    }

    /**
     * Ends the editing session.
     */
    public void stopEditing() {
        editing = false;
        editingTargetIndex = -1;
        currentInput = "";
        originalName = "";
    }

    /**
     * Returns whether an editing session is currently active.
     */
    public boolean isEditing() {
        return editing;
    }

    /**
     * Validates the current input: non-blank and unique among all items.
     */
    public boolean isValid() {
        if (currentInput.isBlank()) return false;
        List<T> items = access.getAll();
        for (T item : items) {
            int indexItem = items.indexOf(item);
            if (indexItem != editingTargetIndex && access.getName(indexItem).equals(currentInput)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Stops editing if the item being edited has changed and the input is no longer valid.
     */
    public void stopEditingIfChanged(int changedItemIndex) {
        if (changedItemIndex == editingTargetIndex && !isValid()) {
            stopEditing();
        }
    }
}