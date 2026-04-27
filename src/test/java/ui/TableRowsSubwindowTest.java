package ui;

import domain.*;
import ui.controllers.TableController;
import ui.editors.RowValueEditor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TableRowsSubwindowTest {

    private Table mockTable;
    private TableController mockController;
    private RowValueEditor mockEditor;
    private List<Column> mockColumns;
    private List<Row> mockRows;
    private TableRowsSubwindow subwindow;
    private Graphics mockGraphics;

    @BeforeEach
    void setUp() throws Exception {
        mockTable = mock(Table.class);
        mockController = mock(TableController.class);
        mockEditor = mock(RowValueEditor.class);
        mockGraphics = mock(Graphics.class);

        // Setup columns
        Column stringCol = mock(Column.class);
        when(stringCol.getName()).thenReturn("Name");
        when(stringCol.getType()).thenReturn(ColumnType.STRING);
        when(stringCol.isBlanksAllowed()).thenReturn(true);

        Column booleanCol = mock(Column.class);
        when(booleanCol.getName()).thenReturn("Active");
        when(booleanCol.getType()).thenReturn(ColumnType.BOOLEAN);
        when(booleanCol.isBlanksAllowed()).thenReturn(true);

        mockColumns = List.of(stringCol, booleanCol);

        // Setup rows
        Row row1 = mock(Row.class);
        when(row1.getValue(0)).thenReturn("Test");
        when(row1.getValue(1)).thenReturn("true");

        Row row2 = mock(Row.class);
        when(row2.getValue(0)).thenReturn("Another");
        when(row2.getValue(1)).thenReturn("false");

        mockRows = List.of(row1, row2);

        // Create subwindow with injected mocks
        subwindow = new TableRowsSubwindow(10, 10, 300, 200, "Test Table", mockController);

        // Inject mocks via reflection
        Field controllerField = TableRowsSubwindow.class.getDeclaredField("controller");
        controllerField.setAccessible(true);
        controllerField.set(subwindow, mockController);

        Field editorField = TableRowsSubwindow.class.getDeclaredField("rowValueEditor");
        editorField.setAccessible(true);
        editorField.set(subwindow, mockEditor);

        // Setup mock behavior
        when(mockController.columnsRequest()).thenReturn(mockColumns);
        when(mockController.getRowsCount()).thenReturn(mockRows.size());
        when(mockController.getTableName()).thenReturn("TestTable");

    }

    @Test
    void constructor_shouldThrowOnNullTable() {
        assertThrows(NullPointerException.class,
                () -> new TableRowsSubwindow(0, 0, 100, 100, "Test", null));
    }

    @Test
    void draw_shouldRenderColumnHeaders() {
        subwindow.draw(mockGraphics, false);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockGraphics, atLeast(2)).drawString(stringCaptor.capture(), anyInt(), anyInt());
        assertTrue(stringCaptor.getAllValues().contains("Name"));
        assertTrue(stringCaptor.getAllValues().contains("Active"));
    }

    @Test
    void draw_shouldHighlightSelectedRow() throws Exception {
        Field selectedRowField = TableRowsSubwindow.class.getDeclaredField("selectedRowIndex");
        selectedRowField.setAccessible(true);
        selectedRowField.set(subwindow, 1);

        subwindow.draw(mockGraphics, true);

        InOrder inOrder = inOrder(mockGraphics);
        inOrder.verify(mockGraphics).setColor(Color.BLUE);
        inOrder.verify(mockGraphics).fillRect(anyInt(), anyInt(), anyInt(), eq(UIConstants.ROW_HEIGHT));
    }

    @Test
    void handleMouseEvent_outsideWindow_shouldDeselectAndCommitEdit() {
        when(mockEditor.isEditing()).thenReturn(true);
        when(mockEditor.isValid()).thenReturn(true);

        subwindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 1);
        verify(mockEditor).commitEdit();
    }

    @Test
    void handleMouseEvent_notClicked_shouldReturnEarly() {
        clearInvocations(mockController);
        subwindow.handleMouseEvent(MouseEvent.MOUSE_MOVED, 20, 20, 0);
        verifyNoMoreInteractions(mockController);
    }

    @Test
    void handleMouseEvent_invalidEditor_shouldReturnEarly() {
        clearInvocations(mockController);
        when(mockEditor.isValid()).thenReturn(false);
        subwindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 20, 20, 1);
        verifyNoInteractions(mockController);
    }

    @Test
    void handleMouseEvent_editingClickOutside_shouldCommitEdit() {
        when(mockEditor.isEditing()).thenReturn(true);
        when(mockEditor.isValid()).thenReturn(true);
        when(mockEditor.getEditingColIndex()).thenReturn(0);

        subwindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 200, 200, 1);
        verify(mockEditor).commitEdit();
    }

    @Test
    void handleKeyEvent_deleteKeyWithSelection_shouldDeleteRow() throws Exception {
        Field selectedRowField = TableRowsSubwindow.class.getDeclaredField("selectedRowIndex");
        selectedRowField.setAccessible(true);
        selectedRowField.set(subwindow, 0);

        subwindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_DELETE,
                KeyEvent.CHAR_UNDEFINED);
        verify(mockController).handleDeleteRowRequest(0);
    }

    @Test
    void handleKeyEvent_deleteKeyWhileEditing_shouldNotDelete() {
        when(mockEditor.isEditing()).thenReturn(true);
        subwindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_DELETE, KeyEvent.CHAR_UNDEFINED);
        verify(mockController, never()).handleDeleteRowRequest(anyInt());
    }

    @Test
    void isInAddRowArea_shouldReturnFalseForInvalidArea() throws Exception {
        Method method = TableRowsSubwindow.class.getDeclaredMethod("isInAddRowArea", int.class, int.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(subwindow, subwindow.x + 10, subwindow.y + 50);
        assertFalse(result);
    }

    @Test
    void handleMouseEvent_doubleClickInAddRowArea_shouldCreateRow() throws Exception {

        when(mockEditor.isEditing()).thenReturn(false);
        when(mockEditor.isValid()).thenReturn(true);
        when(mockController.getRowsCount()).thenReturn(mockRows.size());
        when(mockController.getColumnsCount()).thenReturn(1);

        int rowOffset = mockRows.size() * UIConstants.ROW_HEIGHT;
        int yPos = subwindow.getListTopY() + rowOffset + UIConstants.HEADER_HEIGHT - 5;

        Method isInAddRowArea = TableRowsSubwindow.class.getDeclaredMethod("isInAddRowArea", int.class, int.class);
        isInAddRowArea.setAccessible(true);
        assertTrue((boolean) isInAddRowArea.invoke(subwindow, subwindow.x + 20, yPos));

        // Act
        subwindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, subwindow.x + 20, yPos, 2);

        // Assert
        verify(mockController).handleCreateNewRowRequest();
    }

}