package domain.undoLogic;

import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ColumnDefaultValueCommandTest {

    private Table mockTable;
    private ColumnDefaultValueCommand command;

    @BeforeEach
    void setUp() {
        mockTable = mock(Table.class);
        when(mockTable.getDefaultValue(0)).thenReturn("oldValue");

        command = new ColumnDefaultValueCommand(mockTable, 0, "newValue");
    }

    @Test
    void testExecute_setsNewDefaultValue() {
        command.execute();
        verify(mockTable).setDefaultValue(0, "newValue");
    }

    @Test
    void testUndo_restoresOldDefaultValue() {
        command.execute();
        command.undo();
        verify(mockTable).setDefaultValue(0, "oldValue");
    }

    @Test
    void testConstructorThrowsOnNegativeIndex() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ColumnDefaultValueCommand(mock(Table.class), -1, "someValue");
        }, "Expected constructor to throw for negative index");
    }

    @Test
    void testConstructorThrowsOnNullTable() {
        assertThrows(NullPointerException.class, () -> {
            new ColumnDefaultValueCommand(null, 0, "someValue");
        }, "Expected constructor to throw for null table");
    }
}
