package ui.interaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.Subwindow;
import ui.SubwindowManager;

import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class DragStrategyTest {

    private DragStrategy strategy;
    private Subwindow mockWindow;
    private SubwindowManager mockManager;

    @BeforeEach
    void setUp() {
        strategy = new DragStrategy();
        mockWindow = mock(Subwindow.class);
        mockManager = mock(SubwindowManager.class);

        when(mockWindow.getX()).thenReturn(100);
        when(mockWindow.getY()).thenReturn(100);
    }

    @Test
    void wantsToHandle_ReturnsTrue_WhenInTitleBar() {
        when(mockWindow.isInTitleBar(110, 110)).thenReturn(true);
        assertTrue(strategy.wantsToHandle(mockWindow, 110, 110, 1)); // Added clickCount
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenNotInTitleBar() {
        when(mockWindow.isInTitleBar(150, 200)).thenReturn(false);
        assertFalse(strategy.wantsToHandle(mockWindow, 150, 200, 1)); // Added clickCount
    }

    @Test
    void handle_MovesSubwindow_WhenDragging() {
        when(mockWindow.isInTitleBar(120, 120)).thenReturn(true);

        // Simulate MOUSE_PRESSED at (120,120)
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_PRESSED, 120, 120, 1);
        verify(mockManager).bringToFront(mockWindow);

        // Simulate dragging to (140,150)
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_DRAGGED, 140, 150, 1);

        // Should set position to (140 - 20, 150 - 20) = (120, 130)
        verify(mockWindow).setPosition(120, 130);
    }

    @Test
    void handle_DoesNotMove_WhenNotDragging() {
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_DRAGGED, 200, 200, 1);
        verify(mockWindow, never()).setPosition(anyInt(), anyInt());
    }

    @Test
    void handle_ClearsDraggingSubwindow_OnRelease() {
        // Simulate full drag sequence
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_PRESSED, 110, 110, 1);
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_DRAGGED, 130, 140, 1);
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_RELEASED, 130, 140, 1);

        // Try dragging again (nothing should happen)
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_DRAGGED, 150, 150, 1);

        // Only one setPosition should be called
        verify(mockWindow, times(1)).setPosition(anyInt(), anyInt());
    }

    @Test
    void handle_IgnoresMouseMoved() {
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_MOVED, 150, 150, 1);
        verifyNoInteractions(mockWindow, mockManager);
    }

    @Test
    void handle_NullWindow_DoesNothing() {
        strategy.handle(mockManager, null, MouseEvent.MOUSE_PRESSED, 100, 100, 1);
        // No exception = pass
    }

    @Test
    void handle_NullManager_DoesNothing() {
        strategy.handle(null, mockWindow, MouseEvent.MOUSE_PRESSED, 100, 100, 1);
        // No exception = pass
    }

}
