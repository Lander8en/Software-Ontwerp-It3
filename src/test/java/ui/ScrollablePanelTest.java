package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScrollablePanelTest {

    private ScrollablePanel panel;
    private Graphics graphics;
    private Rectangle viewport;

    @BeforeEach
    void setUp() {
        panel = new ScrollablePanel();
        graphics = mock(Graphics.class);
        viewport = new Rectangle(0, 0, 100, 100);
    }

    @Test
    void testInitialScrollbarsAreNotEnabled() {
        assertFalse(panel.getVerticalScrollbar().isEnabled());
        assertFalse(panel.getHorizontalScrollbar().isEnabled());
    }

    @Test
    void testSetViewportAndContentSizeEnablesScrollbars() {
        panel.setViewportSize(100, 100);
        panel.setContentSize(200, 300);

        assertTrue(panel.getVerticalScrollbar().isEnabled());
        assertTrue(panel.getHorizontalScrollbar().isEnabled());

        assertTrue(panel.getVerticalScrollbar().getThumbSize() > 0);
        assertTrue(panel.getHorizontalScrollbar().getThumbSize() > 0);
    }

    @Test
    void testScrollXClampingWithinBounds() {
        panel.setViewportSize(100, 100);
        panel.setContentSize(200, 100);

        panel.setScrollX(-10);
        assertEquals(0, panel.getScrollX());

        panel.setScrollX(300); // beyond max
        assertEquals(100, panel.getScrollX()); // 200 - 100 = max scrollX

        panel.setScrollX(50);
        assertEquals(50, panel.getScrollX());
    }

    @Test
    void testScrollYClampingWithinBounds() {
        panel.setViewportSize(100, 100);
        panel.setContentSize(100, 300);

        panel.setScrollY(-5);
        assertEquals(0, panel.getScrollY());

        panel.setScrollY(500);
        assertEquals(200, panel.getScrollY()); // 300 - 100 = max scrollY

        panel.setScrollY(75);
        assertEquals(75, panel.getScrollY());
    }

    @Test
    void testDrawScrollbarsWhenBothEnabled() {
        panel.setViewportSize(100, 100);
        panel.setContentSize(200, 200); // both scrollbars enabled

        panel.drawScrollbars(graphics, viewport, true);

        // Should invoke draw twice — once for each scrollbar
        verify(graphics, atLeast(2)).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testDrawScrollbarsWhenOnlyVerticalEnabled() {
        panel.setViewportSize(100, 100);
        panel.setContentSize(100, 200); // only vertical scrollbar

        panel.drawScrollbars(graphics, viewport, true);

        verify(graphics, atLeastOnce()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testDrawScrollbarsWhenOnlyHorizontalEnabled() {
        panel.setViewportSize(100, 100);
        panel.setContentSize(200, 100); // only horizontal scrollbar

        panel.drawScrollbars(graphics, viewport, true);

        verify(graphics, atLeastOnce()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testApplyScrollAndResetScroll() {
        panel.setViewportSize(100, 100);
        panel.setContentSize(200, 200);
        panel.setScrollX(20);
        panel.setScrollY(30);

        // Mocks clip to avoid null pointer
        when(graphics.getClip()).thenReturn(null);

        panel.applyScroll(graphics, viewport);
        verify(graphics).setClip(viewport);
        verify(graphics).translate(-20, -30);

        panel.resetScroll(graphics);
        verify(graphics).translate(20, 30);
        verify(graphics).setClip(null);
    }

    @Test
    void testGettersReflectSetValues() {
        panel.setViewportSize(80, 90);
        panel.setContentSize(180, 190);

        assertEquals(180, panel.getContentWidth());
        assertEquals(190, panel.getContentHeight());

        panel.setScrollX(50);
        panel.setScrollY(60);
        assertEquals(50, panel.getScrollX());
        assertEquals(60, panel.getScrollY());
    }
}
