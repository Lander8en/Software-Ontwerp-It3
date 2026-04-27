package ui.interaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.Subwindow;
import ui.SubwindowManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EditAnySubwindowStrategyTest {

    private EditAnySubwindowStrategy strategy;
    private Subwindow mockWindow;
    private SubwindowManager mockManager;

    @BeforeEach
    void setUp() {
        strategy = new EditAnySubwindowStrategy();
        mockWindow = mock(Subwindow.class);
        mockManager = mock(SubwindowManager.class);
    }

    @Test
    void wantsToHandle_ReturnsTrue_WhenInEditingZone() {
        when(mockWindow.isInEditingZone(10, 20)).thenReturn(true);

        boolean result = strategy.wantsToHandle(mockWindow, 10, 20, 1);

        assertTrue(result);
        verify(mockWindow).isInEditingZone(10, 20);
    }

    @Test
    void wantsToHandle_ReturnsFalse_WhenNotInEditingZone() {
        when(mockWindow.isInEditingZone(50, 50)).thenReturn(false);

        boolean result = strategy.wantsToHandle(mockWindow, 50, 50, 1);

        assertFalse(result);
        verify(mockWindow).isInEditingZone(50, 50);
    }

    @Test
    void handle_DelegatesToWindowHandleMouseEvent() {
        strategy.handle(mockManager, mockWindow, 1, 30, 40, 2);

        verify(mockWindow).handleMouseEvent(1, 30, 40, 2);
        verifyNoMoreInteractions(mockWindow);
        verifyNoInteractions(mockManager);
    }
}
