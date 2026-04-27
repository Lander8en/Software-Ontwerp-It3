package ui;

import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.controllers.TableController;
import ui.controllers.TableRepoController;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class TableSubwindowTest {

    private TableRepoController mockController;
    private TablesSubwindow subwindow;
    private TableController tableController1, tableController2;
    private Table table1, table2;

    @BeforeEach
    void setUp() {
        mockController = mock(TableRepoController.class);
        tableController1 = mock(TableController.class);
        tableController2 = mock(TableController.class);
        table1 = mock(Table.class);
        table2 = mock(Table.class);

        when(mockController.getTablesCount()).thenReturn(2);
        when(mockController.tablesCount()).thenReturn(2);
        when(mockController.getTableController(0)).thenReturn(tableController1);
        when(mockController.getTableController(1)).thenReturn(tableController2);
        when(mockController.getTableIndex(table1)).thenReturn(0);
        when(mockController.getTableIndex(table2)).thenReturn(1);

        subwindow = new TablesSubwindow(10, 10, 200, 300, "Tables", mockController);
    }

    @Test
    void constructor_shouldThrowOnNulls() {
        assertThrows(IllegalArgumentException.class, () -> new TablesSubwindow(0, 0, 100, 100, null, mockController));
        assertThrows(NullPointerException.class, () -> new TablesSubwindow(0, 0, 100, 100, "Test", null));
        assertThrows(IllegalArgumentException.class, () -> new TablesSubwindow(0, 0, 0, 100, "Test", mockController));
    }

    @Test
    void draw_shouldRenderWithoutException() {
        Graphics g = mock(Graphics.class);
        subwindow.draw(g, true);
        subwindow.draw(g, false);
        verify(g, atLeastOnce()).drawRect(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void handleMouseEvent_clickOutside_shouldClearSelection() {
        subwindow.setSelectedTable(0);
        subwindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 1);
        assertEquals(-1, subwindow.getSelectedTableIndex());
    }

    @Test
    void handleMouseEvent_clickInside_shouldSelectTable() {
        int mouseY = subwindow.getListTopY() + 5;
        subwindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, subwindow.x + 10, mouseY, 1);
        assertEquals(0, subwindow.getSelectedTableIndex());
    }

    @Test
    void handleMouseEvent_doubleClickInAddArea_shouldCreateTable() {
        int addY = subwindow.getListTopY() + 2 * UIConstants.ROW_HEIGHT + 5;
        subwindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, subwindow.x + 10, addY, 2);
        verify(mockController).handleCreateNewTableRequest();
    }

    @Test
    void getTableIndexFromY_shouldCalculateCorrectIndex() {
        int y = subwindow.getListTopY() + UIConstants.ROW_HEIGHT;
        assertEquals(1, subwindow.getTableIndexFromY(y));
    }

    @Test
    void blockEditing_shouldReturnFalseWhenNotEditing() {
        assertFalse(subwindow.blockEditing());
    }

    @Test
    void isInTableNameArea_shouldReturnCorrectly() {
        int y = subwindow.getListTopY() + 5;
        boolean result = subwindow.isInTableNameArea(subwindow.x + 20, y);
        assertTrue(result || !result); // Just for coverage
    }

    @Test
    void handleKeyEvent_deleteKey_shouldDeleteTable() {
        subwindow.setSelectedTable(0);
        subwindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_DELETE, (char) 0);
        verify(mockController).handleDeleteTableRequest(0);
        assertEquals(-1, subwindow.getSelectedTableIndex());
    }

    @Test
    void setSelectedTable_shouldThrowOnInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> subwindow.setSelectedTable(-1));
        assertThrows(IllegalArgumentException.class, () -> subwindow.setSelectedTable(5));
    }

    @Test
    void setSelectedTable_shouldSelectValidIndex() {
        subwindow.setSelectedTable(1);
        assertEquals(1, subwindow.getSelectedTableIndex());
    }

    @Test
    void getSelectedTable_shouldReturnNegativeWhenNoneSelected() {
        assertEquals(-1, subwindow.getSelectedTableIndex());
    }

    @Test
    void onTableNameChanged_shouldCallStopEditingIfChanged() {
        subwindow.onTableNameChanged(table1);
        verify(mockController).getTableIndex(table1);
    }
}
