package domain.undoLogic;

import domain.Row;
import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteRowCommandTest {

    private Table mockTable;
    private Row mockRow;
    private DeleteRowCommand command;

    @BeforeEach
    void setUp() {
        mockTable = mock(Table.class);
        mockRow = mock(Row.class);

        // Simulate that table.getRow(2) returns mockRow
        when(mockTable.getRow(2)).thenReturn(mockRow);

        command = new DeleteRowCommand(mockTable, 2);
    }

    @Test
    void testExecute_deletesRowAtGivenIndex() {
        command.execute();
        verify(mockTable).deleteRow(2);
    }

    @Test
    void testUndo_restoresRowAtGivenIndex() {
        command.undo();
        verify(mockTable).addRow(2, mockRow);
    }

    @Test
    void testConstructorThrowsOnNullTable() {
        assertThrows(NullPointerException.class, () -> {
            new DeleteRowCommand(null, 2);
        }, "Expected constructor to throw NullPointerException for null table");
    }
}
