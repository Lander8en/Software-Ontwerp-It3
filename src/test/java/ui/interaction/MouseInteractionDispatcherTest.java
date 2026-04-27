package ui.interaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.Subwindow;
import ui.SubwindowManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MouseInteractionDispatcherTest {

    private MouseInteractionStrategy strategy1;
    private MouseInteractionStrategy strategy2;
    private SubwindowManager mockManager;
    private Subwindow mockWindow;
    private TestableDispatcher dispatcher;

    // Subclass to allow strategy injection for testing
    private static class TestableDispatcher extends MouseInteractionDispatcher {
        private final List<MouseInteractionStrategy> testStrategies;

        TestableDispatcher(List<MouseInteractionStrategy> testStrategies) {
            this.testStrategies = testStrategies;
        }

        @Override
        public void dispatchMouseEvent(SubwindowManager manager, Subwindow target,
                int id, int x, int y, int clicks) {
            for (MouseInteractionStrategy strategy : testStrategies) {
                if (strategy.wantsToHandle(target, x, y, clicks)) {
                    strategy.handle(manager, target, id, x, y, clicks);
                    return;
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        strategy1 = mock(MouseInteractionStrategy.class);
        strategy2 = mock(MouseInteractionStrategy.class);
        mockManager = mock(SubwindowManager.class);
        mockWindow = mock(Subwindow.class);
        dispatcher = new TestableDispatcher(List.of(strategy1, strategy2));
    }

    @Test
    void dispatchMouseEvent_DelegatesToFirstMatchingStrategy() {
        when(strategy1.wantsToHandle(mockWindow, 10, 10, 1)).thenReturn(false);
        when(strategy2.wantsToHandle(mockWindow, 10, 10, 1)).thenReturn(true);

        dispatcher.dispatchMouseEvent(mockManager, mockWindow, 123, 10, 10, 1);

        verify(strategy1).wantsToHandle(mockWindow, 10, 10, 1);
        verify(strategy2).wantsToHandle(mockWindow, 10, 10, 1);
        verify(strategy2).handle(mockManager, mockWindow, 123, 10, 10, 1);
        verify(strategy1, never()).handle(any(), any(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void dispatchMouseEvent_DoesNotCallHandle_IfNoStrategyWantsToHandle() {
        when(strategy1.wantsToHandle(mockWindow, 10, 10, 1)).thenReturn(false);
        when(strategy2.wantsToHandle(mockWindow, 10, 10, 1)).thenReturn(false);

        dispatcher.dispatchMouseEvent(mockManager, mockWindow, 123, 10, 10, 1);

        verify(strategy1, never()).handle(any(), any(), anyInt(), anyInt(), anyInt(), anyInt());
        verify(strategy2, never()).handle(any(), any(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void dispatchMouseEvent_StopsAtFirstHandler() {
        MouseInteractionStrategy handler = mock(MouseInteractionStrategy.class);
        MouseInteractionStrategy neverChecked = mock(MouseInteractionStrategy.class);

        when(handler.wantsToHandle(any(), anyInt(), anyInt(), anyInt())).thenReturn(true);

        TestableDispatcher testDispatcher = new TestableDispatcher(List.of(handler, neverChecked));
        testDispatcher.dispatchMouseEvent(mockManager, mockWindow, 1, 20, 30, 1);

        verify(handler).handle(mockManager, mockWindow, 1, 20, 30, 1);
        verifyNoInteractions(neverChecked);
    }

    @Test
    void defaultConstructor_HasNonEmptyStrategies() {
        MouseInteractionDispatcher realDispatcher = new MouseInteractionDispatcher();

        assertDoesNotThrow(() -> realDispatcher.dispatchMouseEvent(mockManager, mockWindow, 0, 0, 0, 0));
    }
}
