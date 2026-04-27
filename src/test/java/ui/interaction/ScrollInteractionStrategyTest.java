package ui.interaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ScrollablePanel;
import ui.ScrollableWindow;
import ui.Scrollbar;
import ui.Subwindow;
import ui.SubwindowManager;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ScrollInteractionStrategyTest {

    private ScrollInteractionStrategy strategy;
    private SubwindowManager manager;
    private Subwindow subwindow; // Use Subwindow as interface param
    private ScrollableWindow scrollableWindow; // For internal cast
    private ScrollablePanel scrollPanel;
    private Scrollbar verticalScrollbar;
    private Scrollbar horizontalScrollbar;

    @BeforeEach
    void setUp() {
        strategy = new ScrollInteractionStrategy();
        manager = mock(SubwindowManager.class);

        // Create a mock that is actually a Subwindow, but behaves like a
        // ScrollableWindow
        subwindow = mock(Subwindow.class, withSettings().extraInterfaces(ScrollableWindow.class));
        scrollableWindow = (ScrollableWindow) subwindow;

        scrollPanel = mock(ScrollablePanel.class);
        verticalScrollbar = mock(Scrollbar.class);
        horizontalScrollbar = mock(Scrollbar.class);

        when(scrollableWindow.getScrollPanel()).thenReturn(scrollPanel);
        when(scrollableWindow.getViewport()).thenReturn(new Rectangle(0, 0, 100, 100));
        when(scrollPanel.getVerticalScrollbar()).thenReturn(verticalScrollbar);
        when(scrollPanel.getHorizontalScrollbar()).thenReturn(horizontalScrollbar);
        when(verticalScrollbar.isEnabled()).thenReturn(true);
        when(horizontalScrollbar.isEnabled()).thenReturn(true);
    }

    @Test
    void testWantsToHandleVerticalScrollbar() {
        when(verticalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(true);
        assertTrue(strategy.wantsToHandle(subwindow, 95, 50, 1));
    }

    @Test
    void testWantsToHandleHorizontalScrollbar() {
        when(horizontalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(true);
        assertTrue(strategy.wantsToHandle(subwindow, 50, 95, 1));
    }

    @Test
    void testHandleMousePressedVerticalAboveThumb() {
        when(verticalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(true);
        when(verticalScrollbar.getThumbPosition()).thenReturn(50);
        when(verticalScrollbar.getThumbSize()).thenReturn(20);
        when(scrollPanel.getScrollY()).thenReturn(100);
        when(horizontalScrollbar.isEnabled()).thenReturn(true);

        strategy.handle(manager, subwindow, MouseEvent.MOUSE_PRESSED, 95, 40, 1);

        verify(scrollPanel).setScrollY(0);
        verify(verticalScrollbar, never()).setPressed(true);
        verify(manager).bringToFront(subwindow);
    }

    @Test
    void testHandleMousePressedVerticalBelowThumb() {
        when(verticalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(true);
        when(verticalScrollbar.getThumbPosition()).thenReturn(30);
        when(verticalScrollbar.getThumbSize()).thenReturn(20);
        when(scrollPanel.getScrollY()).thenReturn(100);
        when(horizontalScrollbar.isEnabled()).thenReturn(true);

        strategy.handle(manager, subwindow, MouseEvent.MOUSE_PRESSED, 95, 80, 1);

        verify(scrollPanel).setScrollY(200);
        verify(verticalScrollbar, never()).setPressed(true);
        verify(manager).bringToFront(subwindow);
    }

    @Test
    void testHandleMouseDraggedVertical() {
        when(verticalScrollbar.isPressed()).thenReturn(true);
        when(verticalScrollbar.getThumbSize()).thenReturn(20);
        when(verticalScrollbar.getPosition()).thenReturn(10);
        when(scrollPanel.getContentHeight()).thenReturn(500);
        when(scrollableWindow.getViewport()).thenReturn(new Rectangle(0, 0, 100, 100));

        strategy.handle(manager, subwindow, MouseEvent.MOUSE_DRAGGED, 95, 60, 1);

        verify(scrollPanel).setScrollY(anyInt());
        verify(manager).bringToFront(subwindow);
    }

    @Test
    void testHandleMouseDraggedHorizontal() {
        when(horizontalScrollbar.isPressed()).thenReturn(true);
        when(horizontalScrollbar.getThumbSize()).thenReturn(20);
        when(horizontalScrollbar.getPosition()).thenReturn(5);
        when(scrollPanel.getContentWidth()).thenReturn(300);
        when(scrollableWindow.getViewport()).thenReturn(new Rectangle(0, 0, 100, 100));

        strategy.handle(manager, subwindow, MouseEvent.MOUSE_DRAGGED, 50, 95, 1);

        verify(scrollPanel).setScrollX(anyInt());
        verify(manager).bringToFront(subwindow);
    }

    @Test
    void testHandleMouseReleased() {
        strategy.handle(manager, subwindow, MouseEvent.MOUSE_RELEASED, 0, 0, 1);

        verify(verticalScrollbar).setPressed(false);
        verify(horizontalScrollbar).setPressed(false);
        verify(manager).bringToFront(subwindow);
    }

    @Test
    void testWantsToHandleReturnsFalseIfNotScrollableWindow() {
        Subwindow subwindow = mock(Subwindow.class); // not a ScrollableWindow
        boolean result = strategy.wantsToHandle(subwindow, 100, 100, 1);
        assertFalse(result);
    }

    @Test
    void testHandleMousePressedVerticalOnThumb() {
        when(verticalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(true);
        when(verticalScrollbar.getThumbPosition()).thenReturn(30);
        when(verticalScrollbar.getThumbSize()).thenReturn(20);
        when(horizontalScrollbar.isEnabled()).thenReturn(true);

        strategy.handle(manager, subwindow, MouseEvent.MOUSE_PRESSED, 95, 35, 1);

        verify(verticalScrollbar).setPressed(true);
        verify(verticalScrollbar).setPosition(5);
        verify(manager).bringToFront(subwindow);
    }

    @Test
    void testHandleMousePressedHorizontalOnThumb() {
        when(verticalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(false);
        when(horizontalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(true);
        when(horizontalScrollbar.getThumbPosition()).thenReturn(30);
        when(horizontalScrollbar.getThumbSize()).thenReturn(20);
        when(scrollPanel.getScrollX()).thenReturn(50);
        when(verticalScrollbar.isEnabled()).thenReturn(true);

        strategy.handle(manager, subwindow, MouseEvent.MOUSE_PRESSED, 35, 95, 1);

        verify(horizontalScrollbar).setPressed(true);
        verify(horizontalScrollbar).setPosition(5);
        verify(manager).bringToFront(subwindow);
    }

    @Test
    void testHandleMousePressedNeitherScrollbarHit() {
        when(verticalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(false);
        when(horizontalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(false);

        strategy.handle(manager, subwindow, MouseEvent.MOUSE_PRESSED, 50, 50, 1);

        verify(manager).bringToFront(subwindow);
        verify(verticalScrollbar, never()).setPressed(true);
        verify(horizontalScrollbar, never()).setPressed(true);
    }

    @Test
    void testHandleMouseDraggedWhenNoScrollbarPressed() {
        when(verticalScrollbar.isPressed()).thenReturn(false);
        when(horizontalScrollbar.isPressed()).thenReturn(false);

        strategy.handle(manager, subwindow, MouseEvent.MOUSE_DRAGGED, 50, 50, 1);

        verify(scrollPanel, never()).setScrollX(anyInt());
        verify(scrollPanel, never()).setScrollY(anyInt());
        verify(manager).bringToFront(subwindow);
    }

    @Test
    void testWantsToHandleReturnsFalseWhenContainsFalse() {
        when(verticalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(false);
        when(horizontalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(false);

        assertFalse(strategy.wantsToHandle(subwindow, 10, 10, 1));
    }

    @Test
    void testHandleMousePressedWithOnlyVerticalScrollbarEnabled() {
        when(verticalScrollbar.contains(anyInt(), anyInt(), any())).thenReturn(true);
        when(horizontalScrollbar.isEnabled()).thenReturn(false);
        when(verticalScrollbar.getThumbPosition()).thenReturn(30);
        when(verticalScrollbar.getThumbSize()).thenReturn(20);
        when(scrollPanel.getScrollY()).thenReturn(100);

        strategy.handle(manager, subwindow, MouseEvent.MOUSE_PRESSED, 95, 80, 1);

        verify(scrollPanel).setScrollY(200);
        verify(manager).bringToFront(subwindow);

        verify(verticalScrollbar, never()).setPressed(true);
    }

}
