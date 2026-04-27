package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import domain.Column;
import domain.ColumnType;
import domain.ColumnTypeValidator;
import domain.Table;
import domain.observers.RowValueChangeObserver;
import domain.observers.TableNameChangeObserver;
import static ui.UIConstants.CLOSE_BUTTON_SIZE;
import static ui.UIConstants.HEADER_HEIGHT;
import static ui.UIConstants.MARGIN_WIDTH;
import static ui.UIConstants.PADDING;
import static ui.UIConstants.ROW_HEIGHT;
import static ui.UIConstants.TITLE_BAR_HEIGHT;
import ui.controllers.TableController;
import ui.editors.RowValueAccess;
import ui.editors.RowValueEditor;
import ui.interaction.ColumnResizeStrategy.ResizableColumns;
import ui.layout.LayoutRepository;
import ui.layout.TabularLayout;

/**
 * A subwindow that displays and allows editing of rows in a table.
 * Supports cell editing, boolean toggling, and row deletion.
 */
public class TableRowsSubwindow extends Subwindow implements ScrollableWindow, TableNameChangeObserver, RowValueChangeObserver, ResizableColumns {

    private final TableController controller;
    private final RowValueEditor rowValueEditor;
    private int selectedRowIndex = -1;
    private final ScrollablePanel scrollPanel;
    private final TabularLayout layout;


    /**
     * Constructs a new TableRowsSubwindow.
     *
     * @param x      the x-coordinate of the window
     * @param y      the y-coordinate of the window
     * @param width  the width of the window
     * @param height the height of the window
     * @param title  the window title
     */
    public TableRowsSubwindow(int x, int y, int width, int height, String title, TableController controller) {
        super(x, y, width, height, title);
        this.controller = controller;
        this.rowValueEditor = new RowValueEditor(new RowValueAccess(controller));
        this.scrollPanel = new ScrollablePanel();
        this.layout = LayoutRepository.getInstance().forRows(controller.getTableName());
        controller.addRowValueChangeObserver(this);
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
        return controller.getRowsCount() * ROW_HEIGHT + HEADER_HEIGHT + 50;
    }

    /**
     * @return content width based on column layout
     */
    private int calculateContentWidth() {
        int widthView = 0;
        for (int i = 0; i < columnOrder().size(); i++) {
            widthView += columnWidth(i);
        }
        return controller.getColumnsCount() != 0 ? (widthView) + PADDING * 2 + 50 : width - 2;
    }
    
    /**
     * Draws the contents of the subwindow including headers, rows, and the UI elements.
     *
     * @param g        the Graphics context to draw on
     * @param isActive whether this window is the active one
     */
    @Override
            
    public void draw(Graphics g, boolean isActive) {
        updateViewport();
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        g.setColor(isActive ? Color.BLUE : Color.LIGHT_GRAY);
        g.fillRect(x, y, width, TITLE_BAR_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawString(title, x + 10, y + 20);

        int closeX = x + width - CLOSE_BUTTON_SIZE - PADDING;
        int closeY = y + PADDING;
        g.setColor(Color.RED);
        g.fillRect(closeX, closeY, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE);
        g.setColor(Color.WHITE);
        g.drawString("X", closeX + 6, closeY + 16);

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
        
        // Draw the headers for resizing
        drawHeaders(g, scrollY); 

        // Draw headers (fixed position)
        int headerY = getListTopY() - PADDING - scrollY;
        for (int colIndex = 0; colIndex < controller.getColumnsCount(); colIndex++) {
            int cellX = columnX(colIndex);
            Column column = controller.getColumn(colIndex);
            if (column != null) {
                g.setColor(Color.DARK_GRAY);
                g.drawString(column.getName(), cellX, headerY + 15);
            }
        }

        // Draw visible rows
        int firstVisible = Math.max(0, scrollY / ROW_HEIGHT);
        int lastVisible = Math.min(controller.getRowsCount(), firstVisible + (height / ROW_HEIGHT) + 2);
        
        for (int rowIndex = firstVisible; rowIndex < lastVisible; rowIndex++) {
            
            drawRows(g,scrollY);
        }

        // Draw "add row" instruction
        g.setColor(Color.GRAY);
        g.drawString("Double-click to add a row", 
            x + PADDING, 
            getListTopY()
            + controller.getRowsCount() * ROW_HEIGHT + HEADER_HEIGHT - scrollY);

        // Reset scroll transformation
        scrollPanel.resetScroll(g);

        // Draw scrollbars
        scrollPanel.drawScrollbars(g, viewport, isActive);

    }

    /**
     * Draws all rows and highlights the selected row.
     *
     * @param g the Graphics context
     * @param scrollY   The position of the scrollbar on the y axis
     */
    private void drawRows(Graphics g, int scrollY) {
        Boolean redBorder = false;
        if (controller.getColumnsCount() == 0 || controller.getRowsCount() == 0) return;

        for (int rowIndex = 0; rowIndex < controller.getRowsCount(); rowIndex++) {
            int rowY = getListTopY() + rowIndex * ROW_HEIGHT + HEADER_HEIGHT - scrollY;

            for (int colIndex = 0; colIndex < controller.getColumnsCount(); colIndex++) {
                int cellX = columnX(colIndex);
                Column column = controller.getColumn(colIndex);
                if (column != null) {
                    if (!ColumnTypeValidator.isValidValue(column.getType(), controller.getValue(rowIndex, colIndex), column.isBlanksAllowed())) {
                        redBorder = true;
                    }
                    rowValueEditor.draw(g, rowIndex, colIndex, cellX, rowY, columnWidth(colIndex), column.getType(), redBorder);
                    redBorder = false;
                }
            }
            
            if (rowIndex == selectedRowIndex) {
                int marginX = x + 1;
                g.setColor(Color.BLUE);
                g.fillRect(marginX, rowY - 16, MARGIN_WIDTH - 5, ROW_HEIGHT);
            }
        }
    }

    /**
     * Draws all the headers of the colulns.
     *
     * @param g the Graphics context
     * @param scrollY   The position of the scrollbar on the y axis
     */
    private void drawHeaders(Graphics g, int scrollY) {
        int headerY = TITLE_BAR_HEIGHT - scrollY;
        for (int i = 0; i < columnOrder().size(); i++) {
            String name = columnOrder().get(i);
            int xHeader = columnX(i);
            int w = columnWidth(i);
            g.drawRect(xHeader, TITLE_BAR_HEIGHT, w, UIConstants.HEADER_HEIGHT);
            g.drawString(name, xHeader + UIConstants.PADDING,
                         headerY + UIConstants.HEADER_HEIGHT / 2);
        }
    }

    /**
     * @return The position of the scrollBar on the x axis.
     */
    private int scrollX() {                      
        return scrollPanel != null ? scrollPanel.getScrollX() : 0;
    }
    @Override public int headerLeft()           { 
        return windowOriginX() + UIConstants.PADDING - scrollX(); 
    }
    
    @Override              
    public boolean isInsideHeader(int mouseY) {
        int top = y + TITLE_BAR_HEIGHT - scrollPanel.getScrollY();
        return mouseY >= top && mouseY < top + controller.getColumnsCount() * (PADDING+ROW_HEIGHT);
    }

    @Override public java.util.List<String> columnOrder(){
        return controller.columnsRequest().stream().map(Column::getName).toList();
    }
    @Override public TabularLayout layout()     { return layout; }

    /**
     * @param idx   The index of a certain column
     * @return An int that indicates where the column starts.
     */
    private int columnX(int idx) {
        int x = windowOriginX() + UIConstants.PADDING;   
        for (int i = 0; i < idx; i++)
            x += layout.getWidth(columnOrder().get(i));
        return x;
    }

    /**
     * @param idx   The index of a certain column
     * @return An int that indicates where the width of a column.
     */
    private int columnWidth(int idx) {
        return layout.getWidth(columnOrder().get(idx));
    }

    private int windowOriginX() {             // ← NEW helper
        return getX() + UIConstants.MARGIN_WIDTH;   // getX() comes from Subwindow
    }

    /**
     * Handles key events like deletion of a row.
     *
     * @param id      the key event ID
     * @param keyCode the key code
     * @param keyChar the key character
     */
    @Override
    public void handleKeyEvent(int id, int keyCode, char keyChar) {
        if (id != KeyEvent.KEY_PRESSED) return;

        if (controller.getRowsCount() == 0) return;

        if (keyCode == KeyEvent.VK_DELETE && !rowValueEditor.isEditing()) {
            if (selectedRowIndex >= 0 && selectedRowIndex < controller.getRowsCount()) {
                controller.handleDeleteRowRequest(selectedRowIndex);
                selectedRowIndex = -1;
            
            }
        }
            

        rowValueEditor.handleKeyEvent(id, keyCode, keyChar);
    }

    /**
     * Handles mouse interactions for row selection, editing, and row creation.
     *
     * @param id         the mouse event ID
     * @param mouseX     the x-position of the mouse
     * @param mouseY     the y-position of the mouse
     * @param clickCount the number of clicks
     */      
    public void handleMouseEvent(int id, int mouseX, int mouseY, int clickCount) {
        if (id != MouseEvent.MOUSE_CLICKED) return;
        if (!contains(mouseX, mouseY)) {
            if (rowValueEditor.isEditing() && rowValueEditor.isValid()) {
                rowValueEditor.commitEdit();
            }
            selectedRowIndex = -1;
            return;
        }
        if (!rowValueEditor.isValid()) return;

        if (rowValueEditor.isEditing() && !handleEditingClick(mouseX, mouseY)) {
            return;
        }

        if (clickCount == 2 && isInAddRowArea(mouseX, mouseY)) {
            controller.handleCreateNewRowRequest();
        } else if (clickCount == 1) {
            handleSingleClick(mouseX, mouseY);
        }
    }

    /**
     * Handles single click actions for selection or editing.
     *
     * @param mouseX the x-position of the mouse
     * @param mouseY the y-position of the mouse
     */
            
    private void handleSingleClick(int mouseX, int mouseY) {
        int colIdx = 0;
        int cumulativeWidth = 0;
        while (colIdx < controller.columnsRequest().size() - 1 &&
            mouseX > headerLeft() + cumulativeWidth + columnWidth(colIdx)) {
            cumulativeWidth += columnWidth(colIdx);
            colIdx++;
        }

        // first pixel that belongs to the first data-row (see drawRows: rowY-16)
        int rowIdx = (mouseY - getListTopY() - ROW_HEIGHT + scrollPanel.getScrollY()*2 ) / ROW_HEIGHT;
        if (rowIdx == -1) return;           // outside the grid

        if (controller.getRowsCount() == 0|| controller.getColumnsCount() == 0) return;
        if (rowIdx < 0 || rowIdx >= controller.getRowsCount() || colIdx < 0 || colIdx >= controller.getColumnsCount()) return;
                
        int marginX = x + 1;
        if (isInSelectionMargin(mouseX, marginX)) {
            selectedRowIndex = rowIdx;
            rowValueEditor.stopEditing();
            return;
        }

        Column column = controller.getColumn(colIdx);

        if (controller.getRowsCount() == 0 || column == null) return;

        if (column.getType() == ColumnType.BOOLEAN) {
            String current = controller.getValue(rowIdx, colIdx);
            boolean allowBlank = column.isBlanksAllowed();

            String next = switch (current) {
                case "true" -> "false";
                case "false" -> allowBlank ? "" : "true";
                default -> "true";
            };

                selectedRowIndex = -1;
                controller.setRowValue(rowIdx, colIdx, next);
        } else {
            if (rowValueEditor.wasEditing(rowIdx, colIdx)) {
                rowValueEditor.continueEditing(rowIdx, colIdx);
            } else {
                rowValueEditor.startEditing(rowIdx, colIdx);
            }
        }
    }

    /**
     * Handles clicks while editing to determine if commit or cancel is needed.
     *
     * @param mouseX the x-position of the mouse
     * @param mouseY the y-position of the mouse
     * @return true if the click is accepted, false if invalid
     */
    private boolean handleEditingClick(int mouseX, int mouseY) {
        int editingRowIdx = rowValueEditor.getEditingRowIndex();
        if (editingRowIdx == -1) return false;

        int editingColIdx = rowValueEditor.getEditingColIndex();

        int cellX = columnX(editingColIdx);
        int cellY = getListTopY() + editingRowIdx * ROW_HEIGHT + HEADER_HEIGHT;

        boolean clickedInSameCell =
            mouseX <= cellX + columnWidth(editingColIdx) &&
            mouseY >= cellY - 15 && mouseY <= cellY + 5;

        if (!clickedInSameCell) {
            if (rowValueEditor.isValid()) {
                rowValueEditor.commitEdit();
                return true;
            }
            return false;
        }

        return true;
    }

    /**
     * Determines whether the mouse click occurred in the "add row" area.
     *
     * @param mouseX the x-position of the mouse
     * @param mouseY the y-position of the mouse
     * @return true if the click is in the add row zone
     */
    private boolean isInAddRowArea(int mouseX, int mouseY) {
        int top = getListTopY() + controller.getRowsCount() * getRowHeight() - scrollPanel.getScrollY()*2 + HEADER_HEIGHT-10;
        return mouseY >= top && mouseY <= y + height &&
               mouseX >= x && mouseX <= x + width;
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

    @Override
    public void onTableNameChanged(Table changedTable) {
        String tableName = controller.getTableName();
        if (tableName.equals(changedTable.getName())) {
            this.title = "Table Rows - " + changedTable.getName();
        }
    }


    @Override
    public void onRowValueChange(int rowIndex, int colIndex) {
        rowValueEditor.stopEditingIfChanged(rowIndex, colIndex);
    }
}