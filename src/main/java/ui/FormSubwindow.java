package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import domain.ColumnType;
import domain.observers.RowValueChangeObserver;
import static ui.UIConstants.CLOSE_BUTTON_SIZE;
import static ui.UIConstants.FIELD_HEIGHT;
import static ui.UIConstants.FIELD_PADDING;
import static ui.UIConstants.FIELD_WIDTH;
import static ui.UIConstants.FORM_TOP_PADDING;
import static ui.UIConstants.LABEL_WIDTH;
import static ui.UIConstants.PADDING;
import static ui.UIConstants.ROW_HEIGHT;
import static ui.UIConstants.TITLE_BAR_HEIGHT;
import ui.controllers.TableController;
import ui.editors.RowValueAccess;
import ui.editors.RowValueEditor;

public class FormSubwindow extends Subwindow implements RowValueChangeObserver, ScrollableWindow {

    private final TableController controller;
    private final RowValueEditor valueEditor;
    private int currentRowIndex = 1;
    private boolean ctrlDown = false;
    private final ScrollablePanel scrollPanel;
    private final Map<KeyCombo, Runnable> keyCommands = new HashMap<>();

    /**
     * Constructs a new FormSubwindow to display and edit a single row of a table.
     *
     * @param x         x-position of the window
     * @param y         y-position of the window
     * @param width     width of the window
     * @param height    height of the window
     * @param title     the title to display
     * @param controller the table controller to use
     */
    public FormSubwindow(int x, int y, int width, int height, String title, TableController controller) {
        super(x, y, width, height, title);
        this.controller = controller;
        this.valueEditor = new RowValueEditor(new RowValueAccess(controller));
        controller.addRowValueChangeObserver(this);
        this.scrollPanel = new ScrollablePanel();
        setupkeyCommands();
    }

    /**
     * Returns the controller managing this table view.
     * 
     * @return the TableController instance
     */
    public TableController getController() {
        return controller;
    }

    /**
     * Updates the scrollable viewport dimensions based on content size.
     */
    private void updateViewport() {
        int contentHeight = calculateContentHeight();
        int contentWidth = calculateContentWidth(); 
        int viewportHeight = height - TITLE_BAR_HEIGHT - 2;
        int viewportWidth = width - 2;
        
        scrollPanel.setViewportSize(viewportWidth, viewportHeight);
        scrollPanel.setContentSize(contentWidth, contentHeight);
    }
    
    /**
     * @return total content height based on number of columns
     */
    private int calculateContentHeight() {
        return controller.getColumnsCount() * (ROW_HEIGHT + PADDING) + FORM_TOP_PADDING;
    }

    /**
     * @return content width based on column layout
     */
    private int calculateContentWidth() {
        if (controller.getColumnsCount() == 0) { return 200;}
        return LABEL_WIDTH + FIELD_PADDING + PADDING*2 + 110;
    }

    /**
     * Handles all different type of keyCommands.
     */
    private void setupkeyCommands() {
        keyCommands.put(new KeyCombo(KeyEvent.VK_N, true, false), this::handleAddRow);
        keyCommands.put(new KeyCombo(KeyEvent.VK_D, true, false), this::handleDeleteRow);
        keyCommands.put(new KeyCombo(KeyEvent.VK_PAGE_DOWN, false, false), this::handlePageUp);
        keyCommands.put(new KeyCombo(KeyEvent.VK_PAGE_UP, false, false), this::handlePageDown);
    }

    /**
     * Draws the form layout for a single row.
     * 
     * @param g        the Graphics context to draw on
     * @param isActive whether this window is the active one
     */
    @Override
    public void draw(Graphics g, boolean isActive) {
        updateViewport();
        // Outer border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
        g.setColor(isActive ? Color.BLUE : Color.LIGHT_GRAY);
        g.fillRect(x, y, width, TITLE_BAR_HEIGHT);
        g.setColor(Color.BLACK);

        String tableName = controller.getTableName();
        String titleText = "Form - " + tableName + " (Row " + currentRowIndex + ")";
        g.drawString(titleText, x + 10, y + 20);

        // Close button
        int closeX = x + width - CLOSE_BUTTON_SIZE - PADDING;
        int closeY = y + PADDING;
        g.setColor(Color.RED);
        g.fillRect(closeX, closeY, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE);
        g.setColor(Color.WHITE);
        g.drawString("X", closeX + 6, closeY + 16);

        // Form background
        g.setColor(Color.GRAY);
        g.fillRect(x + 1, y + TITLE_BAR_HEIGHT + 1, width - 2, height - TITLE_BAR_HEIGHT - 2);

        // Check if current row exists
        if (currentRowIndex > controller.getRowsCount()) {
            g.setColor(Color.RED);
            g.drawString("No data for row " + currentRowIndex, x + 20, y + TITLE_BAR_HEIGHT + 40);
            return;
        }

        // Render each field in the form
        int rowIndex = currentRowIndex - 1;

        Rectangle viewport = new Rectangle(
            x + 1, 
            y + TITLE_BAR_HEIGHT + 1, 
            width - 2, 
            height - TITLE_BAR_HEIGHT - 2
        );
        
        scrollPanel.applyScroll(g, viewport);
        int scrollY = scrollPanel.getScrollY();
        int visibleHeight = viewport.height;

        // Calculate visible range
        int firstVisible = Math.max(0, scrollY / ROW_HEIGHT);
        int lastVisible = Math.min(controller.getColumnsCount(), firstVisible + (visibleHeight / ROW_HEIGHT) + 2);

        int fieldY = y + TITLE_BAR_HEIGHT + FORM_TOP_PADDING;
        for (int i = firstVisible; i < lastVisible; i++) {

            // Label background (gray) and text
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x + 20, fieldY, LABEL_WIDTH, FIELD_HEIGHT);
            g.setColor(Color.WHITE);
            g.drawString(controller.getColumnName(i), x + 25, fieldY + 15);

            // Determine red border
            String value = controller.getValue(rowIndex, i);
            boolean redBorder = !domain.ColumnTypeValidator.isValidValue(
                controller.getColumnType(i), value, controller.isBlanksAllowed(i)
            );

            // Field
            int fieldX = x + 20 + LABEL_WIDTH + FIELD_PADDING;

            // Use RowValueEditor to draw value
            valueEditor.draw(g, rowIndex, i, fieldX + 5, fieldY + 15, 94, controller.getColumnType(i), redBorder);
            fieldY += FIELD_HEIGHT + FIELD_PADDING;
        }
        // Reset scroll transformation
        scrollPanel.resetScroll(g);
        // Draw scrollbars
        scrollPanel.drawScrollbars(g, viewport, isActive);
    }

    /**
     * Handles key press events: navigation and field editing.
     * 
     * @param id      the key event ID
     * @param keyCode the key code
     * @param keyChar the key character
     */
    @Override
    public void handleKeyEvent(int id, int keyCode, char keyChar) {
        updateCtrl(id, keyCode);

        if (id != KeyEvent.KEY_PRESSED) return;

        Runnable cmd = keyCommands.get(new KeyCombo(keyCode, ctrlDown, false));
        if (cmd != null) {
            cmd.run();
            return;
        }

        valueEditor.handleKeyEvent(id, keyCode, keyChar);
    }

    /**
     * Handles mouse events to trigger field editing or toggling booleans.
     * 
     * @param id         the mouse event ID
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     * @param clickCount the number of clicks
     */
    @Override
    public void handleMouseEvent(int id, int mouseX, int mouseY, int clickCount) {
        if (id != MouseEvent.MOUSE_CLICKED) return;
        if (!contains(mouseX, mouseY)) {
            if (valueEditor.isEditing() && valueEditor.isValid()) {
                valueEditor.commitEdit();
            }
            return;
        }
        if (!valueEditor.isValid()) return;
        if (currentRowIndex > controller.getRowsCount()) return;

        int rowIndex = currentRowIndex - 1;
        int fieldY = y + TITLE_BAR_HEIGHT + FORM_TOP_PADDING;

        for (int colIndex = 0; colIndex < controller.getColumnsCount(); colIndex++) {
            int labelX = x + 20;
            int fieldX = labelX + LABEL_WIDTH + FIELD_PADDING;
            int fieldTop = fieldY;
            int fieldBottom = fieldY + FIELD_HEIGHT;

            if (mouseY >= fieldTop - scrollPanel.getScrollY()*2 && mouseY <= fieldBottom - scrollPanel.getScrollY()*2 &&
                mouseX >= fieldX && mouseX <= fieldX + FIELD_WIDTH) {

                if (controller.getColumnType(colIndex) == ColumnType.BOOLEAN) {
                    String current = controller.getValue(rowIndex, colIndex);
                    boolean allowBlank = controller.isBlanksAllowed(colIndex);
        
                    String next = switch (current) {
                        case "true" -> "false";
                        case "false" -> allowBlank ? "" : "true";
                        default -> "true";
                    };
                    controller.setRowValue(rowIndex, colIndex, next);
                } else {
                    if (valueEditor.wasEditing(rowIndex, colIndex)) {
                        valueEditor.continueEditing(rowIndex, colIndex);
                    } else {
                        valueEditor.startEditing(rowIndex, colIndex);
                    }
                    break;
                }
            }

            fieldY += FIELD_HEIGHT + FIELD_PADDING;
        }
        
    }

    /**
     * Handles changing the Ctrl boolean, when pressed ctrlDown = true, else false
     * 
     * @param id      the key event ID
     * @param keyCode the key code
     */
    private void updateCtrl(int id, int keyCode) {
        if (keyCode == KeyEvent.VK_CONTROL) {
            switch (id) {
                case KeyEvent.KEY_PRESSED -> ctrlDown = true;
                case KeyEvent.KEY_RELEASED -> ctrlDown = false;
            }
        }
    }

    /**
     * Handles adding a row through the controller.
     */
    private void handleAddRow() {
        controller.handleCreateNewRowRequest();
        currentRowIndex = controller.getRowsCount();
    }

    /**
     * Handles deleting the current row through the controller.
     */
    private void handleDeleteRow() {
        if (currentRowIndex >= 1 && currentRowIndex <= controller.getRowsCount()) {
            controller.handleDeleteRowRequest(currentRowIndex - 1);
        }
    }

    /**
     * Handles going to the next row.
     */
    private void handlePageUp() {
        currentRowIndex++;
    }

    /**
     * Handles going to the previous row, unless already on the last row.
     */
    private void handlePageDown() {
        if (currentRowIndex > 1) {
            currentRowIndex--;
        }
    }

    @Override
    public ScrollablePanel getScrollPanel() {
        return scrollPanel;
    }

    @Override
    public Rectangle getViewport() {
        return new Rectangle(
            x + 1,
            y + TITLE_BAR_HEIGHT + 1, 
            width - 2,
            height - TITLE_BAR_HEIGHT - 2
        );
    }

    /**
     * Called when a row value has changed externally.
     * Stops editing if the edited cell is no longer valid.
     */
    @Override
    public void onRowValueChange(int rowIndex, int colIndex) {
        valueEditor.stopEditingIfChanged(rowIndex, colIndex);
    }

    @Override
    public String getTableName() {
        return controller.getTableName();
    }
}