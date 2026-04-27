package domain.undoLogic;

import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ChangeRowValueCommandTest {

    private Table mockTable;
    private ChangeRowValueCommand command;

    @BeforeEach
    void setUp() {
        mockTable = mock(Table.class);
        when(mockTable.getValue(0, 1)).thenReturn("OldValue");
        command = new ChangeRowValueCommand(mockTable, 0, 1, "NewValue");
    }

    @Test
    void testExecute_setsNewValue() {
        command.execute();
        verify(mockTable).setRowValue(0, 1, "NewValue");
    }

    @Test
    void testUndo_restoresOldValue() {
        command.undo();
        verify(mockTable).setRowValue(0, 1, "OldValue");
    }

    @Test
    void testConstructorThrowsOnNegativeRowIndex() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ChangeRowValueCommand(mockTable, -1, 1, "SomeValue");
        }, "Expected constructor to throw for negative rowIndex");
    }

    @Test
    void testConstructorThrowsOnNegativeColIndex() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ChangeRowValueCommand(mockTable, 0, -1, "SomeValue");
        }, "Expected constructor to throw for negative colIndex");
    }
}
