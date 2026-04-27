package ui.interaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.Subwindow;
import ui.Subwindow.ResizeZone;
import ui.SubwindowManager;

import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResizeStrategyTest {

    private ResizeStrategy strategy;
    private Subwindow mockWindow;
    private SubwindowManager mockManager;

    @BeforeEach
    void setUp() {
        strategy = new ResizeStrategy();
        mockWindow = mock(Subwindow.class);
        mockManager = mock(SubwindowManager.class);
    }

    @Test
    void wantsToHandle_ReturnsTrue_WhenInResizeZone() {
        when(mockWindow.getResizeZone(10, 10)).thenReturn(ResizeZone.BOTTOM_RIGHT);
        assertTrue(strategy.wantsToHandle(mockWindow, 10, 10, 1)); // Updated
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenNotInResizeZone() {
        when(mockWindow.getResizeZone(10, 10)).thenReturn(ResizeZone.NONE);
        assertFalse(strategy.wantsToHandle(mockWindow, 10, 10, 1)); // Updated
    }

    @Test
    void handle_MousePressed_SetsInitialStateAndBringsToFront() {
        when(mockWindow.getResizeZone(100, 100)).thenReturn(ResizeZone.RIGHT);

        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_PRESSED, 100, 100, 1);

        verify(mockManager).bringToFront(mockWindow);
    }

    @Test
    void handle_MouseDragged_DelegatesResize_WhenResizing() {
        // Setup for a previous press
        when(mockWindow.getResizeZone(100, 100)).thenReturn(ResizeZone.RIGHT);
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_PRESSED, 100, 100, 1);

        // Act: simulate drag
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_DRAGGED, 120, 130, 1);

        // 120 - 100 = 20, 130 - 100 = 30
        verify(mockWindow).applyResize(ResizeZone.RIGHT, 20, 30, 100, 100);
    }

    @Test
    void handle_MouseDragged_DoesNothing_IfNotInitialized() {
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_DRAGGED, 120, 130, 1);
        verify(mockWindow, never()).applyResize(any(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void handle_MouseReleased_ClearsResizingState() {
        when(mockWindow.getResizeZone(50, 50)).thenReturn(ResizeZone.TOP);
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_PRESSED, 50, 50, 1);

        // Release event
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_RELEASED, 60, 60, 1);

        // Then drag again — nothing should happen
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_DRAGGED, 70, 70, 1);
        verify(mockWindow, never()).applyResize(ResizeZone.TOP, 20, 20, 100, 100);
    }
}
