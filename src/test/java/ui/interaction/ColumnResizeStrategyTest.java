package ui.interaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.Subwindow;
import ui.SubwindowManager;
import ui.layout.TabularLayout;

import java.awt.event.MouseEvent;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColumnResizeStrategyTest {

    private ColumnResizeStrategy strategy;
    private SubwindowManager mockManager;
    private TabularLayout mockLayout;
    private Subwindow mockSubwindow;
    private ColumnResizeStrategy.ResizableColumns mockView;

    @BeforeEach
    void setUp() {
        strategy = new ColumnResizeStrategy();
        mockManager = mock(SubwindowManager.class);
        mockLayout = mock(TabularLayout.class);

        // Create a mock that implements both Subwindow and ResizableColumns
        mockSubwindow = mock(Subwindow.class,
                withSettings().extraInterfaces(ColumnResizeStrategy.ResizableColumns.class));
        mockView = (ColumnResizeStrategy.ResizableColumns) mockSubwindow;
    }

    @Test
    void wantsToHandle_ReturnsTrue_WhenNearResizableColumnBorder() {
        when(mockView.isInsideHeader(10)).thenReturn(true);
        when(mockView.headerLeft()).thenReturn(0);
        when(mockView.columnOrder()).thenReturn(List.of("A", "B"));
        when(mockView.layout()).thenReturn(mockLayout);
        when(mockView.isColumnResizable("A")).thenReturn(true);
        when(mockLayout.getWidth("A")).thenReturn(50);
        when(mockView.isColumnResizable("B")).thenReturn(true);
        when(mockLayout.getWidth("B")).thenReturn(50);

        // x = 98 is within HOT_ZONE = 3 of the right edge of column B (50 + 50 = 100)
        boolean result = strategy.wantsToHandle(mockSubwindow, 98, 10, 1);

        assertTrue(result);
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenOutsideHeader() {
        when(mockView.isInsideHeader(100)).thenReturn(false);

        boolean result = strategy.wantsToHandle(mockSubwindow, 50, 100, 1);
        assertFalse(result);
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenNotResizableColumn() {
        when(mockView.isInsideHeader(10)).thenReturn(true);
        when(mockView.headerLeft()).thenReturn(0);
        when(mockView.columnOrder()).thenReturn(List.of("A"));
        when(mockView.isColumnResizable("A")).thenReturn(false);
        when(mockView.layout()).thenReturn(mockLayout);
        when(mockLayout.getWidth("A")).thenReturn(50);

        boolean result = strategy.wantsToHandle(mockSubwindow, 48, 10, 1);
        assertFalse(result);
    }

    @Test
    void handle_StartsAndPerformsDrag_WhenMouseEventsOccur() {
        when(mockView.headerLeft()).thenReturn(0);
        when(mockView.columnOrder()).thenReturn(List.of("A"));
        when(mockView.isColumnResizable("A")).thenReturn(true);
        when(mockView.layout()).thenReturn(mockLayout);
        when(mockLayout.getWidth("A")).thenReturn(50);

        // Start drag
        strategy.handle(mockManager, mockSubwindow, MouseEvent.MOUSE_PRESSED, 52, 0, 1);

        // Perform drag
        strategy.handle(mockManager, mockSubwindow, MouseEvent.MOUSE_DRAGGED, 60, 0, 1);

        // Check new width = 50 + (60 - 52)
        verify(mockLayout).setWidth("A", 58);
    }

    @Test
    void handle_EndsDrag_WhenMouseReleased() {
        when(mockView.headerLeft()).thenReturn(0);
        when(mockView.columnOrder()).thenReturn(List.of("A"));
        when(mockView.isColumnResizable("A")).thenReturn(true);
        when(mockView.layout()).thenReturn(mockLayout);
        when(mockLayout.getWidth("A")).thenReturn(50);

        strategy.handle(mockManager, mockSubwindow, MouseEvent.MOUSE_PRESSED, 52, 0, 1);
        strategy.handle(mockManager, mockSubwindow, MouseEvent.MOUSE_DRAGGED, 60, 0, 1);
        strategy.handle(mockManager, mockSubwindow, MouseEvent.MOUSE_RELEASED, 60, 0, 1);

        // Drag should end here — further drag should do nothing
        strategy.handle(mockManager, mockSubwindow, MouseEvent.MOUSE_DRAGGED, 70, 0, 1);

        // Only one width change should have occurred
        verify(mockLayout, times(1)).setWidth("A", 58);
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenSubwindowDoesNotImplementResizableColumns() {
        Subwindow mockBasicWindow = mock(Subwindow.class);
        boolean result = strategy.wantsToHandle(mockBasicWindow, 50, 10, 1);
        assertFalse(result);
    }

    @Test
    void handle_DoesNothing_WhenSubwindowDoesNotImplementResizableColumns() {
        Subwindow mockBasicWindow = mock(Subwindow.class);
        // No exception should occur and nothing should be called
        strategy.handle(mockManager, mockBasicWindow, MouseEvent.MOUSE_PRESSED, 50, 10, 1);
        strategy.handle(mockManager, mockBasicWindow, MouseEvent.MOUSE_DRAGGED, 60, 10, 1);
        strategy.handle(mockManager, mockBasicWindow, MouseEvent.MOUSE_RELEASED, 60, 10, 1);
        strategy.handle(mockManager, mockBasicWindow, MouseEvent.MOUSE_EXITED, 60, 10, 1);
        // Nothing to verify — success = no crash and internal state not leaked
    }

    @Test
    void handle_ClearsDragState_WhenMouseExited() {
        when(mockView.headerLeft()).thenReturn(0);
        when(mockView.columnOrder()).thenReturn(List.of("A"));
        when(mockView.isColumnResizable("A")).thenReturn(true);
        when(mockView.layout()).thenReturn(mockLayout);
        when(mockLayout.getWidth("A")).thenReturn(50);

        strategy.handle(mockManager, mockSubwindow, MouseEvent.MOUSE_PRESSED, 52, 0, 1);
        strategy.handle(mockManager, mockSubwindow, MouseEvent.MOUSE_EXITED, 52, 0, 1);

        // Internal drag state should be cleared
        strategy.handle(mockManager, mockSubwindow, MouseEvent.MOUSE_DRAGGED, 60, 0, 1);
        // Should only be one width update before EXITED
        verify(mockLayout, times(0)).setWidth("A", 58);
    }

}
