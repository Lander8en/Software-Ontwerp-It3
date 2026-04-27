package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubwindowTest {

    private static class TestSubwindow extends Subwindow {
        public TestSubwindow(int x, int y, int width, int height, String title) {
            super(x, y, width, height, title);
        }

        @Override
        public void draw(Graphics g, boolean isActive) {
            // no-op
        }

        @Override
        public void handleKeyEvent(int id, int keyCode, char keyChar) {
            // no-op
        }

        @Override
        public void handleMouseEvent(int id, int x, int y, int clickCount) {
            // no-op
        }

        @Override
        public String getTableName() {
            return "TestTable";
        }
    }

    private TestSubwindow window;

    @BeforeEach
    void setUp() {
        window = new TestSubwindow(10, 20, 200, 100, "Test Window");
    }

    @Test
    void constructor_invalidWidthHeight_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new TestSubwindow(0, 0, 0, 10, "Invalid"));
        assertThrows(IllegalArgumentException.class, () -> new TestSubwindow(0, 0, 10, 0, "Invalid"));
    }

    @Test
    void constructor_nullTitle_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new TestSubwindow(0, 0, 10, 10, null));
    }

    @Test
    void getX_getY_returnCorrectValues() {
        assertEquals(10, window.getX());
        assertEquals(20, window.getY());
    }

    @Test
    void setPosition_updatesPosition() {
        window.setPosition(30, 40);
        assertEquals(30, window.getX());
        assertEquals(40, window.getY());
    }

    @Test
    void contains_correctlyDetectsInsideAndOutside() {
        assertTrue(window.contains(15, 25));
        assertFalse(window.contains(5, 5));
    }

    @Test
    void isInTitleBar_correctlyDetectsZone() {
        assertTrue(window.isInTitleBar(50, 21));
        assertFalse(window.isInTitleBar(50, 100));
    }

    @Test
    void isInCloseButton_correctlyDetectsZone() {
        int closeX = 10 + 200 - UIConstants.CLOSE_BUTTON_SIZE - UIConstants.PADDING;
        int closeY = 20 + UIConstants.PADDING;
        assertTrue(window.isInCloseButton(closeX, closeY));
        assertFalse(window.isInCloseButton(0, 0));
    }

    @Test
    void isInSelectionMargin_correctlyDetectsMargin() {
        assertTrue(window.isInSelectionMargin(10, 10));
        assertFalse(window.isInSelectionMargin(1000, 10));
    }

    @Test
    void isOnScrollBar_and_isInScrollBar_delegateTo_isInside() {
        assertTrue(window.isInScrollBar(10, 10));
        assertTrue(window.isOnScrollBar(10, 10));
    }

    @Test
    void getListTopY_returnsCorrectY() {
        assertEquals(20 + UIConstants.TITLE_BAR_HEIGHT + UIConstants.PADDING, window.getListTopY());
    }

    @Test
    void getRowHeight_returnsConstant() {
        assertEquals(UIConstants.ROW_HEIGHT, window.getRowHeight());
    }

    @Test
    void getResizeZone_correctZonesReturned() {
        int x = window.getX();
        int y = window.getY();
        int w = window.width;
        int h = window.height;

        assertEquals(Subwindow.ResizeZone.TOP_LEFT, window.getResizeZone(x, y));
        assertEquals(Subwindow.ResizeZone.TOP_RIGHT, window.getResizeZone(x + w, y));
        assertEquals(Subwindow.ResizeZone.BOTTOM_LEFT, window.getResizeZone(x, y + h));
        assertEquals(Subwindow.ResizeZone.BOTTOM_RIGHT, window.getResizeZone(x + w, y + h));
        assertEquals(Subwindow.ResizeZone.TOP, window.getResizeZone(x + 10, y));
        assertEquals(Subwindow.ResizeZone.BOTTOM, window.getResizeZone(x + 10, y + h));
        assertEquals(Subwindow.ResizeZone.LEFT, window.getResizeZone(x, y + 10));
        assertEquals(Subwindow.ResizeZone.RIGHT, window.getResizeZone(x + w, y + 10));
        assertEquals(Subwindow.ResizeZone.NONE, window.getResizeZone(x + 10, y + 10));
    }

    @Test
    void applyResize_appliesCorrectlyWithinLimits() {
        window.applyResize(Subwindow.ResizeZone.RIGHT, 20, 0, 100, 50);
        assertEquals(220, window.width);
    }

    @Test
    void applyResize_doesNotApplyIfBelowMinimum() {
        window.applyResize(Subwindow.ResizeZone.RIGHT, -150, 0, 100, 50);
        assertEquals(200, window.width); // unchanged
    }

    @Test
    void isInEditingZone_nonScrollable_returnsTrueIfInside() {
        int insideX = window.x + 10;
        int insideY = window.y + UIConstants.TITLE_BAR_HEIGHT + 5;
        assertTrue(window.isInEditingZone(insideX, insideY));
    }

    @Test
    void isInEditingZone_nonScrollable_returnsFalseIfOutside() {
        int outsideX = window.x - 1;
        int outsideY = window.y - 1;
        assertFalse(window.isInEditingZone(outsideX, outsideY));
    }

    @Test
    void testIsInEditingZone_plainSubwindow() {
        Subwindow subwindow = new Subwindow(10, 10, 200, 100, "Test") {
            @Override
            public void draw(Graphics g, boolean isActive) {
            }

            @Override
            public void handleKeyEvent(int id, int keyCode, char keyChar) {
            }

            @Override
            public void handleMouseEvent(int id, int x, int y, int clickCount) {
            }

            @Override
            public String getTableName() {
                return "PlainTestTable";
            }
        };

        int mx = 20;
        int my = 10 + UIConstants.TITLE_BAR_HEIGHT + 5;
        assertTrue(subwindow.isInEditingZone(mx, my));

        int outY = 10 + subwindow.height + 10;
        assertFalse(subwindow.isInEditingZone(mx, outY));
    }

    @Test
    void testIsInEditingZone_scrollableWindowCombinations() {
        Subwindow subwindow = mock(
                Subwindow.class,
                withSettings()
                        .useConstructor(10, 10, 200, 150, "ScrollTest")
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .extraInterfaces(ScrollableWindow.class));

        when(subwindow.getTableName()).thenReturn("MockedTable");

        Scrollbar hScroll = mock(Scrollbar.class);
        Scrollbar vScroll = mock(Scrollbar.class);
        ScrollablePanel scrollPanel = mock(ScrollablePanel.class);

        when(scrollPanel.getHorizontalScrollbar()).thenReturn(hScroll);
        when(scrollPanel.getVerticalScrollbar()).thenReturn(vScroll);
        when(((ScrollableWindow) subwindow).getScrollPanel()).thenReturn(scrollPanel);

        int mx = 30;
        int my = 10 + UIConstants.TITLE_BAR_HEIGHT + 5;

        when(hScroll.isEnabled()).thenReturn(true);
        when(vScroll.isEnabled()).thenReturn(true);
        assertTrue(subwindow.isInEditingZone(mx, my));

        when(hScroll.isEnabled()).thenReturn(true);
        when(vScroll.isEnabled()).thenReturn(false);
        assertTrue(subwindow.isInEditingZone(mx, my));

        when(hScroll.isEnabled()).thenReturn(false);
        when(vScroll.isEnabled()).thenReturn(true);
        assertTrue(subwindow.isInEditingZone(mx, my));

        when(hScroll.isEnabled()).thenReturn(false);
        when(vScroll.isEnabled()).thenReturn(false);
        assertTrue(subwindow.isInEditingZone(mx, my));
    }
}
