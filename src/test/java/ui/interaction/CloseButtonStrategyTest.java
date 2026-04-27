package ui.interaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.Subwindow;
import ui.SubwindowManager;

import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloseButtonStrategyTest {

    private CloseButtonStrategy strategy;
    private Subwindow mockWindow;
    private SubwindowManager mockManager;

    @BeforeEach
    void setUp() {
        strategy = new CloseButtonStrategy();
        mockWindow = mock(Subwindow.class);
        mockManager = mock(SubwindowManager.class);
    }

    @Test
    void wantsToHandle_ReturnsTrue_WhenClickInCloseButton() {
        when(mockWindow.isInCloseButton(50, 10)).thenReturn(true);

        boolean result = strategy.wantsToHandle(mockWindow, 50, 10, 1); // Added clickCount

        assertTrue(result);
        verify(mockWindow).isInCloseButton(50, 10);
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenClickOutsideCloseButton() {
        when(mockWindow.isInCloseButton(100, 100)).thenReturn(false);

        boolean result = strategy.wantsToHandle(mockWindow, 100, 100, 1); // Added clickCount

        assertFalse(result);
        verify(mockWindow).isInCloseButton(100, 100);
    }

    @Test
    void handle_ClosesSubwindow_WhenMouseClicked() {
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_CLICKED, 20, 20, 1);

        verify(mockManager).closeSubwindow(mockWindow);
    }

    @Test
    void handle_DoesNothing_WhenEventIsNotClick() {
        strategy.handle(mockManager, mockWindow, MouseEvent.MOUSE_MOVED, 20, 20, 1);

        verify(mockManager, never()).closeSubwindow(any());
    }

    @Test
    void handle_DoesNothing_WhenManagerOrWindowIsNull() {
        strategy.handle(null, mockWindow, MouseEvent.MOUSE_CLICKED, 20, 20, 1);
        strategy.handle(mockManager, null, MouseEvent.MOUSE_CLICKED, 20, 20, 1);

        verify(mockManager, never()).closeSubwindow(any());
    }
}
