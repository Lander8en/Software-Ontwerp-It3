package ui;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ui.controllers.TableController;
import ui.editors.ColumnDefaultValueEditor;
import ui.editors.NameEditor;
import ui.layout.TabularLayout;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ui.UIConstants.*;

class TableDesignSubwindowTest {

    private TableController controller;
    private TableDesignSubwindow subwindow;

    @BeforeEach
    void setUp() {
        controller = mock(TableController.class);
        when(controller.getTableName()).thenReturn("TestTable");
        when(controller.getColumnsCount()).thenReturn(0);

        subwindow = new TableDesignSubwindow(0, 0, 300, 300, "Test Window", controller);
    }

    @Test
    void constructorThrowsOnNullTitleOrController() {
        assertThrows(NullPointerException.class, () -> new TableDesignSubwindow(0, 0, 300, 300, null, controller));
        assertThrows(NullPointerException.class, () -> new TableDesignSubwindow(0, 0, 300, 300, "Title", null));
    }

    @Test
    void testDrawCallsExpectedMethods() {
        Graphics g = mock(Graphics.class);
        subwindow.draw(g, true);

        verify(g, atLeastOnce()).drawRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(g, atLeastOnce()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(g, atLeastOnce()).drawString(anyString(), anyInt(), anyInt());
    }

    @Test
    void testOnColumnChangedValidColumn() {
        when(controller.isColumnTypeValid(anyInt())).thenReturn(true);
        subwindow.onColumnChanged(0);
        verify(controller).isColumnTypeValid(0);
    }

    @Test
    void testHandleKeyEventDeleteColumn() {
        when(controller.getColumnsCount()).thenReturn(1);
        subwindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, 10, 50, 1); // Click to select
        subwindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_DELETE, (char) KeyEvent.VK_DELETE);
        verify(controller).handleDeleteColumnRequest(anyInt());
    }

    @Test
    void testHandleClickOutsideCommitsEdits() throws Exception {
        subwindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, -10, -10, 1);

        Field field = TableDesignSubwindow.class.getDeclaredField("selectedColumnIndex");
        field.setAccessible(true);
        int selectedIndex = field.getInt(subwindow);

        assertEquals(-1, selectedIndex);
    }

    @Test
    void testHandleDefaultValueClickBoolean() throws Exception {
        when(controller.typeRequest(0)).thenReturn(ColumnType.BOOLEAN);
        when(controller.isBlanksAllowed(0)).thenReturn(true);
        when(controller.getDefaultValue(0)).thenReturn("true");

        invokePrivateMethod(subwindow, "handleDefaultValueClick", new Class[] { int.class }, 0);
        verify(controller).setDefaultValue(0, "false");
    }

    @Test
    void testGetColumnIndexFromY() throws Exception {
        when(controller.getColumnsCount()).thenReturn(5);

        Method method = TableDesignSubwindow.class.getDeclaredMethod("getColumnListTopY");
        method.setAccessible(true);
        int topY = (int) method.invoke(subwindow);

        int y = topY + ROW_HEIGHT; // Second row
        int index = subwindow.getColumnIndexFromY(y);
        assertEquals(1, index);
    }

    @Test
    void testOnColumnChangedDoesNotThrow() {
        assertDoesNotThrow(() -> subwindow.onColumnChanged(0));
    }

    @Test
    void testDraw_withValidBoundsAndActive() throws Exception {
        Graphics g = mock(Graphics.class);
        subwindow.draw(g, true);
        // No exception = pass
    }

    @Test
    void testHandleBlockedTypeClick_returnsFalseWhenNotBlocked() throws Exception {
        ColumnDefaultValueEditor editor = mock(ColumnDefaultValueEditor.class);
        when(editor.isTypeBlocked()).thenReturn(false);
        setField(subwindow, "defaultValueEditor", editor);

        boolean result = invokeHandleBlockedTypeClick(subwindow, 10, 10);
        assertFalse(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testHandleMarginClick_setsSelectedIndexAndStopsEditing() throws Exception {
        NameEditor<Column> mockEditor = mock(NameEditor.class);
        setField(subwindow, "columnEditor", mockEditor);

        invokePrivateMethod(subwindow, "handleMarginClick", new Class[] { int.class }, 2);

        int selectedColumnIndex = (int) getField(subwindow, "selectedColumnIndex");
        assertEquals(2, selectedColumnIndex);
        verify(mockEditor).stopEditing();
    }

    @SuppressWarnings("unchecked")
    @Test
    void testHandleNameClick_commitsPreviousAndStartsEditing() throws Exception {
        NameEditor<Column> mockEditor = mock(NameEditor.class);
        when(mockEditor.isEditing()).thenReturn(true);
        when(mockEditor.getEditingTargetIndex()).thenReturn(0);
        setField(subwindow, "columnEditor", mockEditor);

        invokePrivateMethod(subwindow, "handleNameClick", new Class[] { int.class }, 1);

        verify(mockEditor).commitEdit();
        verify(mockEditor).startEditing(1);

        Field field = TableDesignSubwindow.class.getDeclaredField("selectedColumnIndex");
        field.setAccessible(true);
        int selectedIndex = (int) field.get(subwindow);
        assertEquals(1, selectedIndex);
    }

    @Test
    void testHandleTypeClick_setsTypeBlockedIfInvalid() throws Exception {
        when(controller.getColumnType(0)).thenReturn(ColumnType.STRING);
        when(controller.isColumnTypeValid(0)).thenReturn(false);

        ColumnDefaultValueEditor mockEditor = mock(ColumnDefaultValueEditor.class);
        setField(subwindow, "defaultValueEditor", mockEditor);

        invokePrivateMethod(subwindow, "handleTypeClick", new Class[] { int.class }, 0);

        verify(controller).setColumnType(0, ColumnType.STRING.next());
        verify(mockEditor).setTypeBlocked(0);
    }

    @Test
    void testHandleBlanksCheckboxClick_togglesBlanksAllowed() throws Exception {
        invokePrivateMethod(subwindow, "handleBlanksCheckboxClick", new Class[] { int.class }, 0);
        verify(controller).toggleBlanksAllowed(0);
    }

    @Test
    void testGetTableName() {
        assertEquals("TestTable", subwindow.getTableName());
    }

    @Test
    void testHandleKeyEventNoSelectionDoesNotCrash() {
        // Ensure selectedColumnIndex = -1
        subwindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_DELETE, (char) KeyEvent.VK_DELETE);
        // Should not throw exception even when no column is selected
    }

    @Test
    void testOnColumnChangedInvalidIndexStillSafe() {
        // Simulate controller not returning true or false (null, e.g.)
        when(controller.isColumnTypeValid(anyInt())).thenReturn(false);
        assertDoesNotThrow(() -> subwindow.onColumnChanged(99));
        verify(controller).isColumnTypeValid(99);
    }

    @Test
    void testHandleTypeClickDoesNotBlockIfValid() throws Exception {
        when(controller.getColumnType(0)).thenReturn(ColumnType.STRING);
        when(controller.isColumnTypeValid(0)).thenReturn(true);

        ColumnDefaultValueEditor mockEditor = mock(ColumnDefaultValueEditor.class);
        setField(subwindow, "defaultValueEditor", mockEditor);

        invokePrivateMethod(subwindow, "handleTypeClick", new Class[] { int.class }, 0);

        verify(controller).setColumnType(0, ColumnType.STRING.next());
        verify(mockEditor, never()).setTypeBlocked(anyInt());
    }

    @Test
    void testHandleDefaultValueClickNonBooleanNoAction() throws Exception {
        when(controller.typeRequest(0)).thenReturn(ColumnType.STRING);
        when(controller.isBlanksAllowed(0)).thenReturn(true);

        // Should not throw or call setDefaultValue
        invokePrivateMethod(subwindow, "handleDefaultValueClick", new Class[] { int.class }, 0);
        verify(controller, never()).setDefaultValue(anyInt(), anyString());
    }

    @Test
    void testDrawInactiveDoesNotThrow() {
        Graphics g = mock(Graphics.class);
        assertDoesNotThrow(() -> subwindow.draw(g, false));
    }

    @Test
    void testHandleMouseEventUnknownTypeDoesNotCrash() {
        subwindow.handleMouseEvent(MouseEvent.MOUSE_ENTERED, 100, 100, 0); // Should be ignored safely
    }

    @Test
    void testCalculateContentWidth() throws Exception {
        Method method = TableDesignSubwindow.class.getDeclaredMethod("calculateContentWidth");
        method.setAccessible(true);

        // Case 1: No columns
        when(controller.getColumnsCount()).thenReturn(0);
        int resultNoColumns = (int) method.invoke(subwindow);
        assertEquals(200, resultNoColumns);

        // Case 2: Some columns and mocked layout
        when(controller.getColumnsCount()).thenReturn(2);

        TabularLayout mockLayout = mock(TabularLayout.class);
        when(mockLayout.getWidth("Default")).thenReturn(100);
        setField(subwindow, "layout", mockLayout);

        int resultWithColumns = (int) method.invoke(subwindow);
        int expected = NAME_AREA_WIDTH + TYPE_AREA_WIDTH + 100 - 80;
        assertEquals(expected, resultWithColumns);
    }

    @Test
    void testOnDefaultValueChangedCallsStopEditingIfChanged() throws Exception {
        // Arrange
        ColumnDefaultValueEditor mockEditor = mock(ColumnDefaultValueEditor.class);
        setField(subwindow, "defaultValueEditor", mockEditor);

        // Act
        subwindow.onDefaultValueChanged(3);

        // Assert
        verify(mockEditor).stopEditingIfChanged(3);
    }

    @Test
    void testBlockInteractionDueToInvalidEditing_blockEditingTrue() throws Exception {
        TableDesignSubwindow spyWindow = spy(subwindow);
        doReturn(true).when(spyWindow).blockEditing();

        boolean result = invokeBlockInteractionDueToInvalidEditing(spyWindow, 10, 10);
        assertTrue(result);
    }

    @Test
    void testIsInNameClickArea_withinAndOutsideBounds() throws Exception {
        // Arrange
        TableController mockController = mock(TableController.class);
        when(mockController.getTableName()).thenReturn("TestTable");
        TableDesignSubwindow sub = new TableDesignSubwindow(0, 0, 300, 300, "Test", mockController);

        // Access the private method via reflection
        Method method = TableDesignSubwindow.class.getDeclaredMethod("isInNameClickArea", int.class, int.class);
        method.setAccessible(true);

        int rowX = 50;

        // Act & Assert
        // Inside the name area
        boolean insideResult = (boolean) method.invoke(sub, rowX + 10, rowX); // within NAME_AREA_WIDTH
        assertTrue(insideResult, "Expected click inside the name area to return true");

        // Outside the name area
        boolean outsideResult = (boolean) method.invoke(sub, rowX + NAME_AREA_WIDTH + 1, rowX); // just beyond the name
                                                                                                // area
        assertFalse(outsideResult, "Expected click outside the name area to return false");
    }

    @Test
    void testOnColumnNameChange_callsStopEditingIfChanged() throws Exception {
        // Arrange
        TableController mockController = mock(TableController.class);
        when(mockController.getTableName()).thenReturn("TestTable");
        TableDesignSubwindow sub = new TableDesignSubwindow(0, 0, 300, 300, "Test", mockController);

        // Replace columnEditor with a mock
        NameEditor<Column> mockEditor = mock(NameEditor.class);
        Field editorField = TableDesignSubwindow.class.getDeclaredField("columnEditor");
        editorField.setAccessible(true);
        editorField.set(sub, mockEditor);

        // Act
        sub.onColumnNameChange(2);

        // Assert
        verify(mockEditor).stopEditingIfChanged(2);
    }

    @Test
    void testOnTableNameChanged_updatesTitleWhenNamesMatch() throws Exception {
        // Arrange
        TableController mockController = mock(TableController.class);
        when(mockController.getTableName()).thenReturn("MatchingTable");

        TableDesignSubwindow sub = new TableDesignSubwindow(0, 0, 300, 300, "Old Title", mockController);

        Table mockTable = mock(Table.class);
        when(mockTable.getName()).thenReturn("MatchingTable");

        // Act
        sub.onTableNameChanged(mockTable);

        // Assert (using reflection to access private 'title' field)
        Field titleField = TableDesignSubwindow.class.getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        String newTitle = (String) titleField.get(sub);

        assertEquals("Table Design - MatchingTable", newTitle);
    }

    @Test
    void testOnTableNameChanged_doesNotUpdateTitleWhenNamesDoNotMatch() throws Exception {
        // Arrange
        TableController mockController = mock(TableController.class);
        when(mockController.getTableName()).thenReturn("OriginalTable");

        TableDesignSubwindow sub = new TableDesignSubwindow(0, 0, 300, 300, "Initial Title", mockController);

        Table mockTable = mock(Table.class);
        when(mockTable.getName()).thenReturn("DifferentTable");

        // Act
        sub.onTableNameChanged(mockTable);

        // Assert
        Field titleField = TableDesignSubwindow.class.getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        String actualTitle = (String) titleField.get(sub);

        assertEquals("Initial Title", actualTitle);
    }

    @Test
    void testGetScrollPanel_returnsSameInstance() {
        ScrollablePanel panel = subwindow.getScrollPanel();
        assertNotNull(panel);
        assertSame(panel, subwindow.getScrollPanel());
    }

    @Test
    void testGetViewport_returnsCorrectRectangle() {
        TableDesignSubwindow sw = new TableDesignSubwindow(10, 20, 300, 400, "Test", controller);

        Rectangle viewport = sw.getViewport();

        int expectedX = 10 + 1;
        int expectedY = 20 + TITLE_BAR_HEIGHT + 1;
        int expectedWidth = 300 - 2;
        int expectedHeight = 400 - TITLE_BAR_HEIGHT - 2;

        assertEquals(new Rectangle(expectedX, expectedY, expectedWidth, expectedHeight), viewport);
    }

    // Helper methods

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static boolean invokeHandleBlockedTypeClick(TableDesignSubwindow subwindow, int x, int y) throws Exception {
        Method method = TableDesignSubwindow.class.getDeclaredMethod("handleBlockedTypeClick", int.class, int.class);
        method.setAccessible(true);
        return (boolean) method.invoke(subwindow, x, y);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }

    private static Object invokePrivateMethod(Object target, String methodName, Class<?>[] paramTypes, Object... args)
            throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private static boolean invokeBlockInteractionDueToInvalidEditing(TableDesignSubwindow subwindow, int x, int y)
            throws Exception {
        Method method = TableDesignSubwindow.class.getDeclaredMethod("blockInteractionDueToInvalidEditing", int.class,
                int.class);
        method.setAccessible(true);
        return (boolean) method.invoke(subwindow, x, y);
    }

}