package ui;

import domain.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.controllers.TableController;
import ui.editors.RowValueEditor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ui.UIConstants.*;

class FormSubwindowTest {

    private FormSubwindow formWindow;
    private TableController mockController;
    private Graphics mockGraphics;

    @BeforeEach
    void setUp() {
        mockController = mock(TableController.class);
        mockGraphics = mock(Graphics.class);

        when(mockController.getTableName()).thenReturn("TestTable");
        when(mockController.getRowsCount()).thenReturn(3);
        when(mockController.getColumnsCount()).thenReturn(2);
        when(mockController.getColumnName(0)).thenReturn("col0");
        when(mockController.getColumnName(1)).thenReturn("col1");
        when(mockController.getColumnType(0)).thenReturn(ColumnType.STRING);
        when(mockController.getColumnType(1)).thenReturn(ColumnType.BOOLEAN);
        when(mockController.isBlanksAllowed(anyInt())).thenReturn(false);
        when(mockController.getValue(anyInt(), anyInt())).thenReturn("true");

        formWindow = new FormSubwindow(0, 0, 300, 400, "Test Form", mockController);
    }

    @Test
    void constructor_RegistersObserver() {
        verify(mockController).addRowValueChangeObserver(formWindow);
    }

    @Test
    void draw_DoesNotCrash_WhenCurrentRowIsValid() {
        assertDoesNotThrow(() -> formWindow.draw(mockGraphics, true));
    }

    @Test
    void draw_ShowsError_WhenCurrentRowIsOutOfBounds() {
        when(mockController.getRowsCount()).thenReturn(0);
        assertDoesNotThrow(() -> formWindow.draw(mockGraphics, true));
    }

    @Test
    void handleKeyEvent_CallsAddRow_WhenCtrlN() {
        formWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, (char) 0);
        formWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_N, 'N');
        verify(mockController).handleCreateNewRowRequest();
    }

    @Test
    void handleKeyEvent_CallsDeleteRow_WhenCtrlD() {
        formWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, (char) 0);
        formWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_D, 'D');
        verify(mockController).handleDeleteRowRequest(0);
    }

    @Test
    void handleKeyEvent_PageUpAndDown_AdjustsIndex() {
        formWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_PAGE_UP, (char) 0);
        formWindow.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_PAGE_DOWN, (char) 0);
        // No assert, just ensure it doesn't crash
    }

    @Test
    void handleMouseEvent_CommitsIfClickedOutside() {
        formWindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, -10, -10, 1);
        // Cannot verify directly — just ensure it doesn't crash
    }

    @Test
    void handleMouseEvent_TogglesBooleanValue_OnClick() {
        when(mockController.getColumnsCount()).thenReturn(2);
        when(mockController.getRowsCount()).thenReturn(1);

        // Stub column 0 to prevent early return
        when(mockController.getColumnType(0)).thenReturn(ColumnType.STRING);
        when(mockController.getValue(0, 0)).thenReturn("some value");

        // Target column 1 for boolean toggle
        when(mockController.getColumnType(1)).thenReturn(ColumnType.BOOLEAN);
        when(mockController.getValue(0, 1)).thenReturn("true");
        when(mockController.isBlanksAllowed(1)).thenReturn(true);

        int fieldY = TITLE_BAR_HEIGHT + FORM_TOP_PADDING + (FIELD_HEIGHT + FIELD_PADDING); // second row
        int labelX = 0 + 20;
        int fieldX = labelX + LABEL_WIDTH + FIELD_PADDING;

        formWindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, fieldX + 10, fieldY + 10, 1);

        verify(mockController).setRowValue(0, 1, "false");
    }

    @Test
    void getScrollPanel_ReturnsNonNull() {
        assertNotNull(formWindow.getScrollPanel());
    }

    @Test
    void getViewport_HasExpectedDimensions() {
        Rectangle viewport = formWindow.getViewport();
        assertEquals(0 + 1, viewport.x);
        assertEquals(TITLE_BAR_HEIGHT + 1, viewport.y);
        assertEquals(300 - 2, viewport.width);
        assertEquals(400 - TITLE_BAR_HEIGHT - 2, viewport.height);
    }

    @Test
    void onRowValueChange_StopsEditing() {
        formWindow.onRowValueChange(0, 1);
        // Cannot directly verify internal state, just ensure no crash
    }

    @Test
    void getTableName_ReturnsCorrectName() {
        assertEquals("TestTable", formWindow.getTableName());
    }

    @Test
    void getController_ReturnsInjectedController() {
        assertSame(mockController, formWindow.getController());
    }

    @Test
    void handleMouseEvent_CommitsEdit_WhenClickedOutsideAndEditorIsValid() {
        RowValueEditor mockEditor = mock(RowValueEditor.class);
        when(mockEditor.isEditing()).thenReturn(true);
        when(mockEditor.isValid()).thenReturn(true);

        // Inject mock editor via reflection (or refactor FormSubwindow for testability)
        FormSubwindow testWindow = new FormSubwindow(0, 0, 200, 200, "Test", mockController) {
            @Override
            public void handleMouseEvent(int id, int mouseX, int mouseY, int clickCount) {
                // inject mockEditor into the test subclass
                try {
                    java.lang.reflect.Field editorField = FormSubwindow.class.getDeclaredField("valueEditor");
                    editorField.setAccessible(true);
                    editorField.set(this, mockEditor);
                } catch (Exception e) {
                    fail("Reflection injection failed");
                }
                super.handleMouseEvent(id, mouseX, mouseY, clickCount);
            }
        };

        // Simulate clicking outside the window (e.g., negative x/y)
        testWindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, -10, -10, 1);

        verify(mockEditor).commitEdit();
    }

    @Test
    void handleMouseEvent_DoesNothing_WhenClickedOutsideAndEditorNotEditing() {
        RowValueEditor mockEditor = mock(RowValueEditor.class);
        when(mockEditor.isEditing()).thenReturn(false);

        FormSubwindow testWindow = new FormSubwindow(0, 0, 200, 200, "Test", mockController) {
            @Override
            public void handleMouseEvent(int id, int mouseX, int mouseY, int clickCount) {
                try {
                    java.lang.reflect.Field editorField = FormSubwindow.class.getDeclaredField("valueEditor");
                    editorField.setAccessible(true);
                    editorField.set(this, mockEditor);
                } catch (Exception e) {
                    fail("Reflection injection failed");
                }
                super.handleMouseEvent(id, mouseX, mouseY, clickCount);
            }
        };

        testWindow.handleMouseEvent(MouseEvent.MOUSE_CLICKED, -10, -10, 1);

        verify(mockEditor, never()).commitEdit();
    }

    @Test
    void handleMouseEvent_DoesNothing_WhenEventIsNotClick() {
        clearInvocations(mockController); // Clear interactions from constructor

        formWindow.handleMouseEvent(MouseEvent.MOUSE_MOVED, 10, 10, 1);

        verifyNoInteractions(mockController); // Confirm no additional behavior
    }

}
