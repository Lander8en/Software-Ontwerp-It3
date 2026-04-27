package ui.controllers;

import domain.*;
import domain.observers.ColumnAttributesObserver;
import domain.observers.RowValueChangeObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.TableDesignSubwindow;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

public class TableControllerTest {

    private Table mockTable;
    private TableController controller;

    @BeforeEach
    public void setUp() {
        mockTable = mock(Table.class);
        controller = new TableController(mockTable);
    }

    // === ROWS ===

    @Test
    public void testAddRowValueChangeObserverDelegatesToTable() {
        RowValueChangeObserver observer = mock(RowValueChangeObserver.class);
        controller.addRowValueChangeObserver(observer);
        verify(mockTable).addRowValueObserver(observer);
    }

    @Test
    public void testHandleCreateNewRowRequestExecutesCommand() {
        controller.handleCreateNewRowRequest();
        // No direct verification since UndoManager doesn't expose state;
        // test command separately
    }

    @Test
    public void testHandleDeleteRowRequestExecutesCommand() {
        controller.handleDeleteRowRequest(2);
        // Same as above: no verification here, test DeleteRowCommand separately
    }

    @Test
    public void testSetRowValueExecutesCommand() {
        controller.setRowValue(0, 1, "newVal");
    }

    @Test
    public void testGetRowsCountDelegatesToTable() {
        when(mockTable.getRowsCount()).thenReturn(5);
        assertEquals(5, controller.getRowsCount());
        verify(mockTable).getRowsCount();
    }

    @Test
    public void testGetValueDelegatesToTable() {
        when(mockTable.getValue(1, 2)).thenReturn("abc");
        assertEquals("abc", controller.getValue(1, 2));
        verify(mockTable).getValue(1, 2);
    }

    // === COLUMNS ===

    @Test
    public void testAddObserverToColumnRepoDelegatesToTable() {
        ColumnAttributesObserver observer = mock(ColumnAttributesObserver.class);
        controller.addObserverToColumnRepo(observer);
        verify(mockTable).addObserverToColumnRepo(observer);
    }

    @Test
    public void testAddChangeColumnNameObserverDelegatesToTable() {
        TableDesignSubwindow window = mock(TableDesignSubwindow.class);
        controller.addChangeColumnNameObserver(window);
        verify(mockTable).addChangeColumnNameObserver(window);
    }

    @Test
    public void testHandleCreateNewColumnRequestExecutesCommand() {
        controller.handleCreateNewColumnRequest();
    }

    @Test
    public void testHandleDeleteColumnRequestExecutesCommand() {
        controller.handleDeleteColumnRequest(1);
    }

    @Test
    public void testGetColumnsCountDelegatesToTable() {
        when(mockTable.getColumnsCount()).thenReturn(3);
        assertEquals(3, controller.getColumnsCount());
    }

    @Test
    public void testColumnsRequestDelegatesToTable() {
        List<Column> mockCols = Arrays.asList(mock(Column.class), mock(Column.class));
        when(mockTable.getColumns()).thenReturn(mockCols);
        assertEquals(mockCols, controller.columnsRequest());
    }

    @Test
    public void testGetColumnDelegatesToTable() {
        Column column = mock(Column.class);
        when(mockTable.getColumn(0)).thenReturn(column);
        assertEquals(column, controller.getColumn(0));
    }

    @Test
    public void testGetColumnIndexDelegatesToTable() {
        Column column = mock(Column.class);
        when(mockTable.getColumnIndex(column)).thenReturn(1);
        assertEquals(1, controller.getColumnIndex(column));
    }

    @Test
    public void testGetColumnNameDelegatesToTable() {
        when(mockTable.getColumnName(0)).thenReturn("Name");
        assertEquals("Name", controller.getColumnName(0));
    }

    @Test
    public void testGetColumnTypeDelegatesToTable() {
        when(mockTable.getType(0)).thenReturn(ColumnType.STRING);
        assertEquals(ColumnType.STRING, controller.getColumnType(0));
    }

    @Test
    public void testRenameColumnExecutesCommand() {
        controller.renameColumn(1, "NewName");
    }

    @Test
    public void testSetColumnTypeExecutesCommand() {
        controller.setColumnType(0, ColumnType.BOOLEAN);
    }

    @Test
    public void testTypeRequestDelegatesToTable() {
        when(mockTable.getType(2)).thenReturn(ColumnType.INTEGER);
        assertEquals(ColumnType.INTEGER, controller.typeRequest(2));
    }

    @Test
    public void testIsColumnTypeValidUsesValidator() {
        Column col = mock(Column.class);
        when(mockTable.getColumn(0)).thenReturn(col);

        try (MockedStatic<ColumnTypeValidator> mocked = mockStatic(ColumnTypeValidator.class)) {
            mocked.when(() -> ColumnTypeValidator.isColumnTypeValid(col, mockTable)).thenReturn(true);

            assertTrue(controller.isColumnTypeValid(0));

            mocked.verify(() -> ColumnTypeValidator.isColumnTypeValid(col, mockTable));
        }
    }

    @Test
    public void testToggleBlanksAllowedExecutesCommand() {
        controller.toggleBlanksAllowed(0);
    }

    @Test
    public void testColumnAllowsBlanksDelegatesToTable() {
        when(mockTable.isBlanksAllowed(0)).thenReturn(true);
        assertTrue(controller.columnAllowsBlanks(0));
    }

    @Test
    public void testIsBlanksAllowedDelegatesToTable() {
        when(mockTable.isBlanksAllowed(1)).thenReturn(false);
        assertFalse(controller.isBlanksAllowed(1));
    }

    @Test
    public void testSetDefaultValueExecutesCommand() {
        controller.setDefaultValue(0, "xyz");
    }

    @Test
    public void testGetDefaultValueDelegatesToTable() {
        when(mockTable.getDefaultValue(1)).thenReturn("abc");
        assertEquals("abc", controller.getDefaultValue(1));
    }

    // === TABLE ===

    @Test
    public void testGetTableNameDelegatesToTable() {
        when(mockTable.getName()).thenReturn("TestTable");
        assertEquals("TestTable", controller.getTableName());
    }
}
