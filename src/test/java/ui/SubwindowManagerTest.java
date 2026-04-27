package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ui.controllers.TableController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SubwindowManagerTest {

    private SubwindowManager manager;

    @BeforeEach
    void setUp() {
        manager = new SubwindowManager();
    }

    @Test
    void addNewTableSubwindow_AddsTableSubwindowToList() {
        manager.addNewTablesSubwindow();
        Subwindow subwindow = manager.findSubwindowAt(50, 50);
        assertNotNull(subwindow);
        assertTrue(subwindow instanceof TablesSubwindow);
    }

    @Test
    void addNewTableDesignSubwindow_AddsDesignSubwindowToList() {
        TableController controller = mock(TableController.class);
        when(controller.getTableName()).thenReturn("Mock");

        manager.addNewTableDesignSubwindow(controller);

        Subwindow subwindow = manager.findSubwindowAt(50, 50);
        assertNotNull(subwindow);
        assertTrue(subwindow instanceof TableDesignSubwindow);
    }

    @Test
    void addNewTableRowsSubwindow_AddsRowsSubwindowToList() {
        TableController controller = mock(TableController.class);
        when(controller.getTableName()).thenReturn("Mock");

        manager.addNewTableRowsSubwindow(controller);

        Subwindow subwindow = manager.findSubwindowAt(50, 50);
        assertNotNull(subwindow);
        assertTrue(subwindow instanceof TableRowsSubwindow);
    }

    @Test
    void bringToFront_MovesSubwindowToEndOfList() {
        manager.addNewTablesSubwindow();
        manager.addNewTablesSubwindow();

        Subwindow first = manager.findSubwindowAt(50, 50);
        Subwindow second = manager.findSubwindowAt(70, 70);

        assertNotNull(first);
        assertNotNull(second);

        manager.bringToFront(first);

        Subwindow top = manager.findSubwindowAt(50, 50);
        assertEquals(first, top);
    }

    @Test
    void handleKeyEvent_CtrlT_AddsNewTableSubwindow() {
        manager.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, ' ');
        manager.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_T, 'T');

        Subwindow top = manager.findSubwindowAt(50, 50);
        assertNotNull(top);
        assertTrue(top instanceof TablesSubwindow);
    }

    @Test
    void handleKeyEvent_CtrlEnter_OpensRelatedSubwindow() {
        TableController controller = mock(TableController.class);
        when(controller.getTableName()).thenReturn("Mock");

        manager.addNewTableDesignSubwindow(controller);

        manager.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, ' ');
        manager.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ENTER, '\n');

        assertNotNull(manager.findSubwindowAt(50, 50)); // Original
        assertNotNull(manager.findSubwindowAt(70, 70)); // New one
    }

    @Test
    void closeSubwindow_RemovesItFromList() {
        manager.addNewTablesSubwindow();
        Subwindow sub = manager.findSubwindowAt(50, 50);
        assertNotNull(sub);

        manager.closeSubwindow(sub);
        assertNull(manager.findSubwindowAt(50, 50));
    }

    @Test
    void drawAll_CallsDrawOnEachSubwindow() {
        Subwindow mock1 = mock(Subwindow.class);
        Subwindow mock2 = mock(Subwindow.class);

        List<Subwindow> list = getSubwindowList(manager);
        list.clear();
        list.add(mock1);
        list.add(mock2);

        Graphics g = mock(Graphics.class);
        manager.drawAll(g);

        verify(mock1).draw(eq(g), eq(false));
        verify(mock2).draw(eq(g), eq(true));
    }

    @Test
    void drawAll_DrawsAllWindowsAndMarksLastAsActive() {
        Graphics graphics = mock(Graphics.class);
        Subwindow window1 = mock(Subwindow.class);
        Subwindow window2 = mock(Subwindow.class);

        manager.addNewTablesSubwindow();
        manager.addNewTablesSubwindow();

        List<Subwindow> testList = List.of(window1, window2);
        setSubwindows(manager, new ArrayList<>(testList));

        manager.drawAll(graphics);

        verify(window1).draw(graphics, false);
        verify(window2).draw(graphics, true);
    }

    @Test
    void findSubwindowAt_ReturnsCorrectWindow() {
        Subwindow w1 = mock(Subwindow.class);
        Subwindow w2 = mock(Subwindow.class);
        when(w1.contains(10, 10)).thenReturn(false);
        when(w2.contains(10, 10)).thenReturn(true);

        setSubwindows(manager, List.of(w1, w2));

        Subwindow result = manager.findSubwindowAt(10, 10);
        assertEquals(w2, result);
    }

    @Test
    void handleMouseEvent_DelegatesToDispatcher() {
        Subwindow target = mock(Subwindow.class);
        when(target.contains(5, 5)).thenReturn(true);

        setSubwindows(manager, new ArrayList<>(List.of(target)));

        manager.handleMouseEvent(1, 5, 5, 1);
    }

    @Test
    void handleKeyEvent_SetsCtrlDownTrueAndFalseCorrectly() {
        manager.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, (char) 0);
        manager.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_T, 'T');
        manager.handleKeyEvent(KeyEvent.KEY_RELEASED, KeyEvent.VK_CONTROL, (char) 0);
        manager.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_T, 'T');

        List<Subwindow> subwindows = getInternalSubwindows(manager);
        assertEquals(1, subwindows.size());
    }

    // --- Helper Methods ---

    @SuppressWarnings("unchecked")
    private List<Subwindow> getSubwindowList(SubwindowManager manager) {
        try {
            var field = SubwindowManager.class.getDeclaredField("subwindows");
            field.setAccessible(true);
            return (List<Subwindow>) field.get(manager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access subwindows list", e);
        }
    }

    private void setSubwindows(SubwindowManager manager, List<Subwindow> list) {
        try {
            var field = SubwindowManager.class.getDeclaredField("subwindows");
            field.setAccessible(true);
            field.set(manager, list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Subwindow> getInternalSubwindows(SubwindowManager manager) {
        try {
            Field field = SubwindowManager.class.getDeclaredField("subwindows");
            field.setAccessible(true);
            return (List<Subwindow>) field.get(manager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
