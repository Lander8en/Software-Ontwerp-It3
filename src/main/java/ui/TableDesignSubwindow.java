package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

import domain.Column;
import domain.ColumnType;
import domain.Table;
import domain.observers.ColumnAttributesObserver;
import domain.observers.ColumnNameChangeObserver;
import domain.observers.TableNameChangeObserver;
import static ui.UIConstants.CLOSE_BUTTON_SIZE;
import static ui.UIConstants.DEFAULT_VALUE_OFFSET;
import static ui.UIConstants.NAME_AREA_WIDTH;
import static ui.UIConstants.PADDING;
import static ui.UIConstants.ROW_HEIGHT;
import static ui.UIConstants.SPACER;
import static ui.UIConstants.TITLE_BAR_HEIGHT;
import static ui.UIConstants.TYPE_AREA_WIDTH;
import ui.controllers.TableController;
import ui.editors.ColumnDefaultValueAccess;
import ui.editors.ColumnDefaultValueEditor;
import ui.editors.ColumnNameAccess;
import ui.editors.ColumnRowRenderer;
import ui.editors.NameEditor;
import ui.interaction.ColumnResizeStrategy.ResizableColumns;
import ui.layout.LayoutRepository;
import ui.layout.TabularLayout;

/**
 * A subwindow that provides an interface to edit the structure (columns) of a table.
 * <p>
 * Allows users to edit column names, types, default values, and constraints such as
 * whether blanks are allowed. Also supports adding and deleting columns.
 */
public class TableDesignSubwindow extends Subwindow implements TableNameChangeObserver, ColumnAttributesObserver, ColumnNameChangeObserver, ScrollableWindow, ResizableColumns {

    private final TableController controller;
    private final NameEditor<Column> columnEditor;
    private final ColumnDefaultValueEditor defaultValueEditor;
    private final ColumnRowRenderer rowRenderer;
    private int selectedColumnIndex = -1;
    private final ScrollablePanel scrollPanel;
    private final TabularLayout layout;


    /**
     * Constructs a TableDesignSubwindow with the given parameters.
     *
     * @param x      the x-position of the window
     * @param y      the y-position of the window
     * @param width  the width of the window
     * @param height the height of the window
     * @param title  the window title
     * @param table  the table whose columns are to be edited
     * @throws NullPointerException if title or table is null
     */
    public TableDesignSubwindow(int x, int y, int width, int height, String title, TableController controller) {
        super(x, y, width, height, Objects.requireNonNull(title, "title must not be null"));
        this.controller = controller;
        this.columnEditor = new NameEditor<>(new ColumnNameAccess(controller));
        this.defaultValueEditor = new ColumnDefaultValueEditor(new ColumnDefaultValueAccess(controller));
        this.rowRenderer = new ColumnRowRenderer(columnEditor, defaultValueEditor, controller);
        controller.addObserverToColumnRepo(this);
        this.scrollPanel = new ScrollablePanel();
        controller.addChangeColumnNameObserver(this);
        this.layout = LayoutRepository.getInstance().forDesign(controller.getTableName());
        updateViewport();
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
        return controller.getColumnsCount() * ROW_HEIGHT + 70;
    }
    
    /**
     * @return content width based on column layout
     */
    private int calculateContentWidth() {
        if (controller.getColumnsCount() == 0) { return 200;}
        return NAME_AREA_WIDTH + TYPE_AREA_WIDTH + layout.getWidth("Default") - 80;
    }

    /**
     * Indicates what needs to happen to a column that is changed, depending
     * on wheter or not editing is blocked.
     * @param changedColIndex   Index of the column that needs to be changed      
     */
    @Override
    public void onColumnChanged(int changedColIndex) {
        columnEditor.stopEditingIfChanged(changedColIndex);

        // Check if this column is now invalid due to default value or cell values
        boolean blocked = !controller.isColumnTypeValid(changedColIndex);

        if (blocked) {
            defaultValueEditor.setTypeBlocked(changedColIndex);
        } else if (defaultValueEditor.isTypeBlocked() && defaultValueEditor.getBlockedColumnIndex() == changedColIndex) {
            defaultValueEditor.clearTypeBlock();
        }
    }

    /**
     * Stops the DefaultValue from editing.
     * @param changedColIndex   Index of the column that needs to be changed      
     */
    @Override
    public void onDefaultValueChanged(int changedColIndex) {
        defaultValueEditor.stopEditingIfChanged(changedColIndex);
    }

    /**
     * Draws the subwindow, including the title bar, close button, and column rows.
     *
     * @param g        the graphics context
     * @param isActive whether this subwindow is currently active
     */
    @Override
    public void draw(Graphics g, boolean isActive) {
        updateViewport();
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        // Title bar
        g.setColor(isActive ? Color.BLUE : Color.LIGHT_GRAY);
        g.fillRect(x, y, width, TITLE_BAR_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawString(title, x + 10, y + 20);

        // Close button
        int closeX = x + width - CLOSE_BUTTON_SIZE - PADDING;
        int closeY = y + PADDING;
        g.setColor(Color.RED);
        g.fillRect(closeX, closeY, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE);
        g.setColor(Color.WHITE);
        g.drawString("X", closeX + 6, closeY + 16);

        // Content area
        g.setColor(Color.WHITE);
        g.fillRect(x + 1, y + TITLE_BAR_HEIGHT + 1, width - 2, height - TITLE_BAR_HEIGHT - 2);

        // Set up viewport rectangle
        Rectangle viewport = new Rectangle(
            x + 1, 
            y + TITLE_BAR_HEIGHT + 1, 
            width - 2, 
            height - TITLE_BAR_HEIGHT - 2
        );

        // Apply scroll transformation
        scrollPanel.applyScroll(g, viewport);
        int scrollY = scrollPanel.getScrollY();
        int visibleHeight = viewport.height;

        // Calculate visible range
        int firstVisible = Math.max(0, scrollY / ROW_HEIGHT);
        int lastVisible = Math.min(controller.getColumnsCount(), firstVisible + (visibleHeight / ROW_HEIGHT) + 2);

        for (int i = firstVisible; i < lastVisible; i++) {
            Column columnCopy = controller.getColumn(i);

            int rowX = columnX(0);
            int rowY = getColumnListTopY() + i * ROW_HEIGHT - scrollY;
            int defaultWidth = (columnCopy.getType() == ColumnType.BOOLEAN)
                    ? 14 /* checkbox size */
                    : layout.getWidth("Default");

            rowRenderer.drawRow(g, i, columnCopy, rowX, rowY, i == selectedColumnIndex, defaultWidth);
        }

        // Draw "add column" instruction
        g.setColor(Color.GRAY);
        g.drawString("Double-click to add a column", 
            x + PADDING, 
            y + 50 + controller.getColumnsCount() * ROW_HEIGHT - scrollY);

        // Reset scroll transformation
        scrollPanel.resetScroll(g);

        // Draw scrollbars
        scrollPanel.drawScrollbars(g, viewport, isActive);
    }

    /**
     * Handles mouse events within the subwindow.
     *
     * @param id         the type of mouse event
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     * @param clickCount the number of clicks
     */
    public void handleMouseEvent(int id, int mouseX, int mouseY, int clickCount) {
        if (handleOutsideClickIfNecessary(mouseX, mouseY)) return;
        if (blockInteractionDueToInvalidEditing(mouseX, mouseY)) return;
        if (handleBlockedTypeClick(mouseX, mouseY)) return;

        if (id == MouseEvent.MOUSE_CLICKED) {
            handleClick(mouseX, mouseY, clickCount);
        }
    }

    /**
     * Handles mouse events when outside the window.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    private boolean handleOutsideClickIfNecessary(int mouseX, int mouseY) {
        if (!contains(mouseX, mouseY)) {
            handleClickOutside();
            return true;
        }
        return false;
    }

    /**
     * Blocks you from doing other things while a value is Invalid.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    private boolean blockInteractionDueToInvalidEditing(int mouseX, int mouseY) {
        if (blockEditing()) return true;

        if (hasBlockingBlankViolation()) {
            int colIndex = getColumnIndexFromY(mouseY);
            if (colIndex < 0 || colIndex >= controller.getColumnsCount()) return true;

            if (!isBlankViolation(colIndex)) return true;
            if (!isInBlanksCheckboxArea(mouseX, getRowX())) return true;
        }

        return false;
    }

    /**
     * Blocks you from changing the Type while blocked.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    private boolean handleBlockedTypeClick(int mouseX, int mouseY) {
        if (defaultValueEditor.isTypeBlocked()) {
            int blockedIndex = defaultValueEditor.getBlockedColumnIndex();
            int hoveredIndex = getColumnIndexFromY(mouseY);
            return hoveredIndex != blockedIndex || !isInTypeClickArea(mouseX, mouseY);
        }
        return false;
    }

    /**
     * Handles mouse events if not blocked.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     * @param clickCount the number of clicks
     */
    private void handleClick(int mouseX, int mouseY, int clickCount) {
        if (clickCount == 2 && isInAddColumnArea(mouseX, mouseY)) {
            controller.handleCreateNewColumnRequest();
        } else if (clickCount == 1) {
            handleDefaultValueCommit(mouseX, mouseY);
            handleClickAt(mouseX, mouseY);
        }
    }

    /**
     * Commits the default value when clicking outside the editing area.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    private void handleDefaultValueCommit(int mouseX, int mouseY) {
        if (!defaultValueEditor.isEditing()) return;

        int editingIndex = defaultValueEditor.getEditingTargetIndex();
        if (editingIndex == -1) return;

        int rowX = getRowX();
        int rowY = getColumnListTopY() + editingIndex * ROW_HEIGHT;
        int boxX = rowX + DEFAULT_VALUE_OFFSET;
        int boxY = rowY;

        int defaultWidth = layout.getWidth("Default");
        boolean clickedInBox = mouseX >= boxX && mouseX <= boxX + defaultWidth &&
                               mouseY >= boxY && mouseY <= boxY + ROW_HEIGHT;

        if (!clickedInBox && defaultValueEditor.isValid()) {
            defaultValueEditor.commitEdit();
        }
    }

    /**
     * Handles single click actions for selection or editing.
     *
     * @param mouseX the x-position of the mouse
     * @param mouseY the y-position of the mouse
     */
    private void handleClickAt(int mouseX, int mouseY) {
        int index = getColumnIndexFromY(mouseY);
        if (index < 0 || index >= controller.getColumnsCount()) {
            commitIfEditing();
            selectedColumnIndex = -1;
            return;
        }
        selectedColumnIndex = index;

        int rowX = getRowX();

        if (isInSelectionMargin(mouseX, rowX)) {
            commitIfEditing();
            handleMarginClick(index);
        } else if (isInNameClickArea(mouseX, rowX)) {
            commitIfEditing();
            handleNameClick(index);
        } else if (isInTypeClickArea(mouseX, mouseY)) {
            commitIfEditing();
            handleTypeClick(index);
        } else if (isInBlanksCheckboxArea(mouseX, rowX)) {
            commitIfEditing();
            handleBlanksCheckboxClick(index);
        } else if (isInDefaultValueArea(mouseX, rowX)) {
            commitIfEditing();
            handleDefaultValueClick(index);
        }
    }

    /**
     * @return certain row index calculated from the x coordinate.
     */
    private int getRowX() {
        return x + 1 - scrollPanel.getScrollX();
    }

    /**
     * Handles clicking outside the editing area.
     */
    private void handleClickOutside() {
        commitIfEditing();
        selectedColumnIndex = -1;
    }

    /**
     * Handles clicking to the left of a column.
     * @param index  The column on which has been clicked
     */
    private void handleMarginClick(int index) {
        selectedColumnIndex = index;
        columnEditor.stopEditing();
    }

    /**
     * Handles clicking on a name.
     * @param colIndex  The column on which has been clicked
     */
    private void handleNameClick(int colIndex) {
        if (isEditingAnotherColumn(colIndex)) {
            columnEditor.commitEdit();
        }
        selectedColumnIndex = colIndex;
        if (!columnEditor.isEditing() || columnEditor.getEditingTargetIndex() != colIndex) {
            columnEditor.startEditing(colIndex);
        }
    }

    /**
     * Handles clicking on a type.
     * @param colIndex  The column on which has been clicked
     */
    private void handleTypeClick(int colIndex) {
        ColumnType next = controller.getColumnType(colIndex).next();
    
        controller.setColumnType(colIndex, next);
    
        boolean valid = controller.isColumnTypeValid(colIndex);
    
        if (valid) {
            defaultValueEditor.clearTypeBlock();
        } else {
            defaultValueEditor.setTypeBlocked(colIndex);
        }
    }

    /**
     * Handles clicking on a blanks checkbox.
     * @param colIndex  The column on which has been clicked
     */
    private void handleBlanksCheckboxClick(int index) {
        controller.toggleBlanksAllowed(index);
    }

    /**
     * Handles clicking on a default value.
     * @param colIndex  The column on which has been clicked
     */
    private void handleDefaultValueClick(int colIndex) {
        if (controller.typeRequest(colIndex) == ColumnType.BOOLEAN) {
            cycleBooleanDefaultValue(colIndex);
        } else {
            if (defaultValueEditor.getEditingTargetIndex() == colIndex) {
                defaultValueEditor.continueEditing(colIndex);
            } else {
                defaultValueEditor.startEditing(colIndex);
            }
        }
    }

    /**
     * @return x coordinate of the scrollbar, depending on wheter there has been scrolled or not.
     */
    private int scrollX() {                      
        return scrollPanel != null ? scrollPanel.getScrollX() : 0;
    }


    @Override public int headerLeft() {
        int base  = windowOriginX() + UIConstants.PADDING - scrollX();
        int sum   = 0;
        for (var c : controller.columnsRequest()) {
            sum += layout.getWidth(c.getName());
        }
        return base + UIConstants.DEFAULT_VALUE_OFFSET - sum;
    }
    
    @Override
    public boolean isInsideHeader(int mouseY) {
        int top = y + TITLE_BAR_HEIGHT - scrollPanel.getScrollY();
        return mouseY >= top && mouseY < top + controller.getColumnsCount()*ROW_HEIGHT;
    }

    @Override public java.util.List<String> columnOrder() {
        java.util.List<String> order = new java.util.ArrayList<>();
        controller.columnsRequest().forEach(col -> order.add(col.getName()));
        order.add("Default");
        return order;
    }
    
    @Override public TabularLayout layout()     { return layout; }

    private int columnX(int idx) {
        int x = windowOriginX() + UIConstants.PADDING;
        for (int i = 0; i < idx; i++)
            x += layout.getWidth(columnOrder().get(i));
        return x;
    }

    private int windowOriginX() {             
        return getX() + UIConstants.MARGIN_WIDTH;
    }

    @Override
    public boolean isColumnResizable(String column) {
        return "Default".equals(column);
    }

    /**
     * Handles key input such as deletion or editing.
     *
     * @param id      the type of key event
     * @param keyCode the key code
     * @param keyChar the character associated with the key
     */
    @Override
    public void handleKeyEvent(int id, int keyCode, char keyChar) {
        if (id != KeyEvent.KEY_PRESSED) return;

        if (keyCode == KeyEvent.VK_DELETE && !columnEditor.isEditing()) {
            if (selectedColumnIndex >= 0 && selectedColumnIndex < controller.getColumnsCount()) {
                controller.handleDeleteColumnRequest(selectedColumnIndex);
                selectedColumnIndex = -1;
            }
        }

        columnEditor.handleKeyEvent(id, keyCode, keyChar);
        defaultValueEditor.handleKeyEvent(id, keyCode, keyChar);
    }

    /**
     * Checks if a certain mouseposition is in the Type area.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    public boolean isInTypeClickArea(int mouseX, int mouseY) {
        int typeAreaXStart = x + SPACER;
        return mouseX >= typeAreaXStart - scrollPanel.getScrollX() && mouseX <= x + TYPE_AREA_WIDTH - scrollPanel.getScrollX();
    }

    /**
     * Checks if a certain mouseposition is in the Name area.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    private boolean isInNameClickArea(int mouseX, int rowX) {
        return mouseX >= rowX  && mouseX <= rowX + NAME_AREA_WIDTH ;
    }

    /**
     * Checks if a certain column is being edited, or if another column is being edited.
     *
     * @param colIndex    The index of the column that wants to be edited.
     */
    private boolean isEditingAnotherColumn(int colIndex) {
        return columnEditor.isEditing() && columnEditor.getEditingTargetIndex() != colIndex;
    }

    /**
     * Checks if a certain mouseposition is in the Blanks area.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    private boolean isInBlanksCheckboxArea(int mouseX, int rowX) {
        int checkboxStart = rowX + SPACER + 115;
        return mouseX >= checkboxStart && mouseX <= checkboxStart + 14;
    }

    /**
     * Checks if a certain mouseposition is in the DefaultValue area.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    private boolean isInDefaultValueArea(int mouseX, int rowX) {
        int startX = rowX + SPACER + 165;
        int defaultWidth = layout.getWidth("Default");
        return mouseX >= startX && mouseX <= startX + defaultWidth;
    }

    /**
     * Checks if a certain mouseposition is in the AddColumns area.
     *
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     */
    private boolean isInAddColumnArea(int mouseX, int mouseY) {
        int top = getColumnListTopY() + controller.getColumnsCount() * ROW_HEIGHT - scrollPanel.getScrollY()*2;
        return mouseY >= top && mouseY <= y + height &&
            mouseX >= x - scrollPanel.getScrollX() && mouseX <= x + width - scrollPanel.getScrollX();
    }

    /**
     * Cycles the default value of a given column with type Boolean.
     *
     * @param columnIndex   The index of the selected Column
     */
    private void cycleBooleanDefaultValue(int columnIndex) {
        String current = controller.getDefaultValue(columnIndex);
        boolean allowBlank = controller.isBlanksAllowed(columnIndex);

        if (allowBlank) {
            switch (current) {
                case "true" -> controller.setDefaultValue(columnIndex, "false");
                case "false" -> controller.setDefaultValue(columnIndex, "");
                default -> controller.setDefaultValue(columnIndex, "true");
            }
        } else {
            controller.setDefaultValue(columnIndex, "true".equals(current) ? "false" : "true");
        }
    }

    private void commitIfEditing() {
        if (columnEditor.isEditing()) {
            columnEditor.commitEdit();
        }
        if (defaultValueEditor.isEditing()) {
            defaultValueEditor.commitEdit();
        }
    }

    /**
     * Retrieves the top Y coordinate of where the list of columns starts.
     */
    private int getColumnListTopY() {
        return y + TITLE_BAR_HEIGHT + PADDING;
    }

    /**
     * Calculates the column index from a given mouseY position.
     * @param mouseY    The y position of a mouse click
     * @return An int that resembles the index a the clicked column.
     */
    public int getColumnIndexFromY(int mouseY) {
        return (mouseY - getColumnListTopY() + scrollPanel.getScrollY()*2) / ROW_HEIGHT;
    }

    /**
     * @return true if editing should be blocked (due to invalid name input).
     */
    public boolean blockEditing() {
        return !columnEditor.isValid() && selectedColumnIndex != -1 && columnEditor.isEditing();
    }

    /**
     * Indicates if there is a blank where there cant be one
     * @param colIndex  The index of the column where the violation is checked
     * @return Boolean true if there is a violation.
     */
    private boolean isBlankViolation(int colIndex) {
        Column column = controller.getColumn(colIndex);
        return !column.isBlanksAllowed() && column.getDefaultValue().isBlank();
    }

    /**
     * Goes over all the columns to check if there is violation
     * @return Boolean that indicates a violation.
     */
    private boolean hasBlockingBlankViolation() {
        List<Column> columns = controller.columnsRequest();
        for (int i = 0; i < columns.size(); i++) {
            if (isBlankViolation(i)) {
                return true;
            }
        }
        return false;
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

    @Override
    public String getTableName() {
        return controller.getTableName();
    }

    /**
     * Changes the title of the window when the tablename has been changed
     * @param changedTable  The table which name has been changed
     */
    @Override
    public void onTableNameChanged(Table changedTable) {
        String tableName = controller.getTableName();
        if (tableName.equals(changedTable.getName())) {
            this.title = "Table Design - " + changedTable.getName();
        }
    }

    /**
     * Changes the column name.
     * @param colIndex  Index of the column that has been changed.
     */
    @Override
    public void onColumnNameChange(int colIndex) {
        columnEditor.stopEditingIfChanged(colIndex);
    }
}