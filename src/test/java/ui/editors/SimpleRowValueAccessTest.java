package ui.editors;

import domain.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.controllers.TableController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleRowValueAccessTest {

    private TableController mockController;
    private RowValueAccess access;

    @BeforeEach
    void setUp() {
        mockController = mock(TableController.class);
        access = new RowValueAccess(mockController);
    }

    @Test
    void getValue_ReturnsCorrectValueFromController() {
        when(mockController.getValue(2, 3)).thenReturn("example");
        String result = access.getValue(2, 3);
        assertEquals("example", result);
    }

    @Test
    void setValue_DelegatesToController() {
        access.setValue(1, 4, "newValue");
        verify(mockController).setRowValue(1, 4, "newValue");
    }

    @Test
    void getType_ReturnsCorrectColumnType() {
        when(mockController.typeRequest(0)).thenReturn(ColumnType.EMAIL);
        ColumnType type = access.getType(0);
        assertEquals(ColumnType.EMAIL, type);
    }

    @Test
    void allowsBlanks_ReturnsTrueOrFalseBasedOnController() {
        when(mockController.columnAllowsBlanks(1)).thenReturn(true);
        assertTrue(access.allowsBlanks(1));

        when(mockController.columnAllowsBlanks(2)).thenReturn(false);
        assertFalse(access.allowsBlanks(2));
    }
}
