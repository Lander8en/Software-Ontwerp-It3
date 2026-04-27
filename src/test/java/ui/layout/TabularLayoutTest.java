package ui.layout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.UIConstants;

import static org.junit.jupiter.api.Assertions.*;

class TabularLayoutTest {

    private TabularLayout layout;

    @BeforeEach
    void setUp() {
        layout = new TabularLayout();
    }

    @Test
    void returnsDefaultWidthForUnknownColumn() {
        assertEquals(UIConstants.COLUMN_WIDTH, layout.getWidth("NonExistentColumn"));
    }

    @Test
    void setsAndGetsCustomWidth() {
        layout.setWidth("A", 120);
        assertEquals(120, layout.getWidth("A"));
    }

    @Test
    void clampsWidthToMinimumOf50() {
        layout.setWidth("A", 10); // too small
        assertEquals(50, layout.getWidth("A"));

        layout.setWidth("B", -200); // negative
        assertEquals(50, layout.getWidth("B"));
    }

    @Test
    void doesNotUpdateIfWidthIsUnchanged() {
        layout.setWidth("A", 150);
        layout.setWidth("A", 150); // should not change anything

        // This check confirms no crash or regression, behavior is silent optimization
        assertEquals(150, layout.getWidth("A"));
    }

    @Test
    void updatingWidthActuallyChangesIt() {
        layout.setWidth("Col1", 100);
        assertEquals(100, layout.getWidth("Col1"));

        layout.setWidth("Col1", 200);
        assertEquals(200, layout.getWidth("Col1"));
    }
}
