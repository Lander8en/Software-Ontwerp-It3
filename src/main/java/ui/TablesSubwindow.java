package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import domain.Table;
import domain.observers.TableNameChangeObserver;
import static ui.UIConstants.CLOSE_BUTTON_SIZE;
import static ui.UIConstants.PADDING;
import static ui.UIConstants.ROW_HEIGHT;
import static ui.UIConstants.SCROLLBAR_SIZE;
import static ui.UIConstants.TITLE_BAR_HEIGHT;
import ui.controllers.TableController;
import ui.controllers.TableRepoController;
import ui.editors.NameEditor;
import ui.editors.TableNameAccess;

/**
 * A subwindow that displays and allows editing of tables in a TableRepository.
 * Supports selection, renaming, and deletion of tables via keyboard and mouse.
 */
public class TablesSubwindow extends Subwindow implements ScrollableWindow, TableNameChangeObserver {
    private final ScrollablePanel scrollPanel;
    private final TableRepoController controller;
    private final NameEditor<Table> tableEditor;
    private int selectedTableIndex = -1;

    /**
     * Constructs a new TableSubwindow with the given position, size, and table
     * repository.
     *
     * @param x               The x-coordinate of the subwindow.
     * @param y               The y-coordinate of the subwindow.
     * @param width           The width of the subwindow.
     * @param height          The height of the subwindow.
     * @param title           The title of the subwindow.
     * @param tableRepository The table repository to manage.
     * @throws NullPointerException     if title or tableRepository is null.
     * @throws IllegalArgumentException if width or height is non-positive.
     */
    public TablesSubwindow(int x, int y, int width, int height, String title, TableRepoController controller) {
        super(x, y, width, height, title);
        this.scrollPanel = new ScrollablePanel();
        this.controller = controller;
        this.tableEditor = new NameEditor<>(new TableNameAccess(controller));
        updateViewport();
    }

    /**
     * Returns the controller managing a certain table.
     * @param index Of a certain table
     * @return the TableController instance
     */
    public TableController getTableController(int index) {
        return controller.getTableController(index);
    }

    /**
     * Updates the scrollable viewport dimensions based on content size.
     */
    private void updateViewport() {
        int contentHeight = calculateContentHeight();
        int viewportHeight = height - TITLE_BAR_HEIGHT - 2; // Subtract title bar and borders
        int viewportWidth = width - 2; // Subtract borders

        scrollPanel.setViewportSize(viewportWidth, viewportHeight);
        scrollPanel.setContentSize(viewportWidth - SCROLLBAR_SIZE, contentHeight);
    }

    /**
     * @return total content height based on number of columns
     */
    private int calculateContentHeight() {
        // Height of all tables + padding + "Double-click to add" text area
        return controller.tablesCount() * ROW_HEIGHT + 70;
    }

    /**
     * @return Index of the selectedTable
     */
    public int getSelectedTableIndex() {
        return selectedTableIndex;
    }

    /**
     * Draws the contents of the subwindow including headers and table names.
     *
     * @param g        the Graphics context to draw on
     * @param isActive whether this window is the active one
     */
    @Override
    public void draw(Graphics g, boolean isActive) {
        updateViewport();

        // Border
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // Title bar
        g.setColor(isActive ? Color.BLUE : Color.LIGHT_GRAY);
        g.fillRect(x, y, width, TITLE_BAR_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawString(title, x + PADDING, y + 20);

        // Close button
        int closeX = x + width - CLOSE_BUTTON_SIZE - PADDING;
        int closeY = y + PADDING;
        g.setColor(Color.RED);
        g.fillRect(closeX, closeY, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE);
        g.setColor(Color.WHITE);
        g.drawString("X", closeX + 6, closeY + 16);

        // Content area background
        g.setColor(Color.WHITE);
        g.fillRect(x + 1, y + TITLE_BAR_HEIGHT + 1, width - 2, height - TITLE_BAR_HEIGHT - 2);

        // Set up viewport rectangle for scrollable area
        Rectangle viewport = new Rectangle(
                x + 1,
                y + TITLE_BAR_HEIGHT + 1,
                width - 2,
                height - TITLE_BAR_HEIGHT - 2);

        // Apply scroll transformation
        scrollPanel.applyScroll(g, viewport);

        // Draw content with scroll offset
        int scrollY = scrollPanel.getScrollY();
        int visibleHeight = viewport.height;

        // Calculate visible range of items
        int firstVisible = Math.max(0, scrollY / ROW_HEIGHT);
        int lastVisible = Math.min(controller.getTablesCount(), firstVisible + (visibleHeight / ROW_HEIGHT) + 2);

        for (int i = firstVisible; i < lastVisible; i++) {
            int rowX = x + 1;
            int rowY = getListTopY() + i * ROW_HEIGHT - scrollY;
            int rowWidth = width - 2;

            if (i == selectedTableIndex) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(rowX, rowY, rowWidth, ROW_HEIGHT);
            }

            tableEditor.drawName(g, i, rowX, rowY, rowWidth, ROW_HEIGHT);
        }

        // Draw "add table" instruction
        g.setColor(Color.GRAY);
        g.drawString("Double-click to add a table",
                x + PADDING,
                y + 50 + controller.getTablesCount() * ROW_HEIGHT - scrollY);

        // Reset scroll transformation
        scrollPanel.resetScroll(g);

        // Draw scrollbars
        scrollPanel.drawScrollbars(g, viewport, isActive);
    }

    /**
     * Handles mouse interactions for table selection, editing, and table creation.
     *
     * @param id         the mouse event ID
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     * @param clickCount the number of clicks
     */
    public void handleMouseEvent(int id, int mouseX, int mouseY, int clickCount) {
        if (!contains(mouseX, mouseY)) {
            handleClickOutside();
            return;
        }

        if (blockEditing())
            return;
        if (id == MouseEvent.MOUSE_CLICKED) {
            if (clickCount == 2 && isInAddTableArea(mouseX, mouseY)) {
                controller.handleCreateNewTableRequest();
                return;
            }

            if (clickCount == 1) {
                handleTableSelection(mouseX, mouseY);
            }
        }
    }

    /**
     * Handles mouse interactions outside the subwindow.
     */
    private void handleClickOutside() {
        if (tableEditor.isEditing())
            tableEditor.commitEdit();
        selectedTableIndex = -1;
    }

    /**
     * Handles table selection depending on the mouse position and the scrollbar position.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    private void handleTableSelection(int mouseX, int mouseY) {
        int index = getTableIndexFromY(mouseY);

        if (index < 0 || index >= controller.getTablesCount()) {
            if (tableEditor.isEditing()) {
                tableEditor.commitEdit();
                selectedTableIndex = -1;
            }
            return;
        }

        int rowX = x + 1;
        if (isInSelectionMargin(mouseX, rowX)) {
            tableEditor.commitEdit();
            selectedTableIndex = index;
            return;
        }

        if (tableEditor.isEditing()) {
            tableEditor.commitEdit();
        }

        selectedTableIndex = index;
        tableEditor.startEditing(index);
    }

    /**
     * Returns the index of the table corresponding to the given y-coordinate.
     */
    public int getTableIndexFromY(int mouseY) {
        return (mouseY - getListTopY() + scrollPanel.getScrollY() * 2) / ROW_HEIGHT;
    }

    /**
     * Checks wheter or not a mouse position is inside the add table area
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     * @return True if inside, else false.
     */
    private boolean isInAddTableArea(int mouseX, int mouseY) {
        int top = getListTopY() + controller.tablesCount() * ROW_HEIGHT - scrollPanel.getScrollY() * 2;
        return isInside(mouseX, mouseY, x, top, width, height - (top - y));
    }

    /**
     * @return true if editing should be blocked (due to invalid name input).
     */
    public boolean blockEditing() {
        return !tableEditor.isValid() && selectedTableIndex != -1 && tableEditor.isEditing();
    }

    /**
     * @return true if the given point is in the table name area (but not the
     * margin).
     */
    public boolean isInTableNameArea(int mouseX, int mouseY) {
        int index = getTableIndexFromY(mouseY);
        int tableSize = controller.tablesCount();
        if (index < 0 || index >= tableSize)
            return false;

        int rowX = x + 1;
        int rowY = getListTopY() + index * ROW_HEIGHT;
        int rowWidth = width - 2;

        boolean insideRow = isInside(mouseX, mouseY, rowX, rowY - scrollPanel.getScrollY() * 2, rowWidth, ROW_HEIGHT);
        return insideRow && !isInSelectionMargin(mouseX, rowX);
    }

    /**
     * Handles key events like deletion of a table.
     *
     * @param id      the key event ID
     * @param keyCode the key code
     * @param keyChar the key character
     */
    @Override
    public void handleKeyEvent(int id, int keyCode, char keyChar) {
        if (id != KeyEvent.KEY_PRESSED) return;

        if (keyCode == KeyEvent.VK_DELETE && !tableEditor.isEditing()) {
            if (blockEditing())
                return;
            if (hasSelectedTable()) {
                controller.handleDeleteTableRequest(selectedTableIndex);
                selectedTableIndex = -1;
            }
        }

        tableEditor.handleKeyEvent(id, keyCode, keyChar);
    }

    /**
     * @return Returns the amount of tables through the controller.
     */
    public int tablesCount() {
        return controller.tablesCount();
    }

    /**
     * Selects a table by its index in the list.
     *
     * @param index Index to select.
     * @throws IllegalArgumentException if index is invalid.
     */
    public void setSelectedTable(int index) {
        if (index < 0 || index >= controller.tablesCount()) {
            throw new IllegalArgumentException("Invalid table index: " + index);
        }
        selectedTableIndex = index;
    }

    /**
     * @return true if a selectedTable is present, else returns false.
     */
    private boolean hasSelectedTable() {
        return selectedTableIndex >= 0 && selectedTableIndex < controller.tablesCount();
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
                height - TITLE_BAR_HEIGHT - 2);
    }

    @Override
    public String getTableName() {
        return "";
    }

    @Override
    public void onTableNameChanged(Table table) {
        int tableIndex = controller.getTableIndex(table);
        tableEditor.stopEditingIfChanged(tableIndex);
    }
}
