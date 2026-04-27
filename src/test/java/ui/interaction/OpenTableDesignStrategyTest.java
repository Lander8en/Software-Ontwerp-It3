package ui.interaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.SubwindowManager;
import ui.TablesSubwindow;
import ui.controllers.TableController;

import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenTableDesignStrategyTest {

    private OpenTableStrategy strategy;
    private TablesSubwindow mockWindow;
    private SubwindowManager mockManager;
    private TableController mockController;

    @BeforeEach
    void setUp() {
        strategy = new OpenTableStrategy();
        mockWindow = mock(TablesSubwindow.class);
        mockManager = mock(SubwindowManager.class);
        mockController = mock(TableController.class);
    }

    @Test
    void wantsToHandle_ReturnsTrue_WhenInNameAreaAndNotBlockedAndDoubleClick() {
        when(mockWindow.isInTableNameArea(10, 20)).thenReturn(true);
        when(mockWindow.blockEditing()).thenReturn(false);

        assertTrue(strategy.wantsToHandle(mockWindow, 10, 20, 2));
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenNotInNameArea() {
        when(mockWindow.isInTableNameArea(10, 20)).thenReturn(false);
        when(mockWindow.blockEditing()).thenReturn(false);

        assertFalse(strategy.wantsToHandle(mockWindow, 10, 20, 2));
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenEditingBlocked() {
        when(mockWindow.isInTableNameArea(10, 20)).thenReturn(true);
        when(mockWindow.blockEditing()).thenReturn(true);

        assertFalse(strategy.wantsToHandle(mockWindow, 10, 20, 2));
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenClickNotDouble() {
        when(mockWindow.isInTableNameArea(10, 20)).thenReturn(true);
        when(mockWindow.blockEditing()).thenReturn(false);

        assertFalse(strategy.wantsToHandle(mockWindow, 10, 20, 1));
    }

    @Test
    void handle_DoesNothing_WhenClickNotDouble() {
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_CLICKED, 10, 50, 1);

        verify(mockManager, never()).addNewTableDesignSubwindow(any());
        verify(mockManager, never()).addNewTableRowsSubwindow(any());
    }

    @Test
    void handle_DoesNothing_WhenNotMouseClicked() {
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_PRESSED, 10, 50, 2);

        verify(mockManager, never()).addNewTableDesignSubwindow(any());
        verify(mockManager, never()).addNewTableRowsSubwindow(any());
    }

    @Test
    void handle_DoesNothing_WhenIndexInvalid() {
        when(mockWindow.getTableIndexFromY(50)).thenReturn(-1);

        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_CLICKED, 10, 50, 2);

        verify(mockManager, never()).addNewTableDesignSubwindow(any());
        verify(mockManager, never()).addNewTableRowsSubwindow(any());
    }

    @Test
    void handle_DoesNothing_WhenIndexOutOfBounds() {
        when(mockWindow.getTableIndexFromY(50)).thenReturn(1);
        when(mockWindow.tablesCount()).thenReturn(1); // index 1 is out of bounds

        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_CLICKED, 10, 50, 2);

        verify(mockManager, never()).addNewTableDesignSubwindow(any());
        verify(mockManager, never()).addNewTableRowsSubwindow(any());
    }

    @Test
    void handle_AddsDesignSubwindow_WhenNoColumns() {
        when(mockWindow.getTableIndexFromY(50)).thenReturn(0);
        when(mockWindow.tablesCount()).thenReturn(1);
        when(mockWindow.getTableController(0)).thenReturn(mockController);
        when(mockController.getColumnsCount()).thenReturn(0);

        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_CLICKED, 10, 50, 2);

        verify(mockWindow).setSelectedTable(0);
        verify(mockManager).addNewTableDesignSubwindow(mockController);
        verify(mockManager, never()).addNewTableRowsSubwindow(any());
    }

    @Test
    void handle_AddsRowsSubwindow_WhenTableHasColumns() {
        when(mockWindow.getTableIndexFromY(50)).thenReturn(0);
        when(mockWindow.tablesCount()).thenReturn(1);
        when(mockWindow.getTableController(0)).thenReturn(mockController);
        when(mockController.getColumnsCount()).thenReturn(3);

        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_CLICKED, 10, 50, 2);

        verify(mockWindow).setSelectedTable(0);
        verify(mockManager).addNewTableRowsSubwindow(mockController);
        verify(mockManager, never()).addNewTableDesignSubwindow(any());
    }
}
