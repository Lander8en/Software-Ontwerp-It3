package domain.undoLogic;

import domain.ColumnType;
import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SetColumnTypeCommandTest {

    private Table mockTable;
    private SetColumnTypeCommand command;

    @BeforeEach
    void setUp() {
        mockTable = mock(Table.class);
        when(mockTable.getType(1)).thenReturn(ColumnType.STRING); // oldType = TEXT
        command = new SetColumnTypeCommand(mockTable, 1, ColumnType.INTEGER); // newType = NUMBER
    }

    @Test
    void testExecute_setsNewColumnType() {
        command.execute();
        verify(mockTable).setColumnType(1, ColumnType.INTEGER);
    }

    @Test
    void testUndo_restoresOldColumnType() {
        command.undo();
        verify(mockTable).setColumnType(1, ColumnType.STRING);
    }

    @Test
    void testConstructorThrowsOnNullTable() {
        assertThrows(NullPointerException.class, () -> {
            new SetColumnTypeCommand(null, 1, ColumnType.STRING);
        }, "Expected constructor to throw NullPointerException for null table");
    }

    @Test
    void testConstructorThrowsOnNullType() {
        assertThrows(NullPointerException.class, () -> {
            new SetColumnTypeCommand(mockTable, 1, null);
        }, "Expected constructor to throw NullPointerException for null type");
    }
}
