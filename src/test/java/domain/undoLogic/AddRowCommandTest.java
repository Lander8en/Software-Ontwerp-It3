package domain.undoLogic;

import domain.Row;
import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AddRowCommandTest {

    private Table mockTable;
    private Row mockRow;
    private AddRowCommand command;

    @BeforeEach
    void setUp() {
        mockTable = mock(Table.class);
        mockRow = mock(Row.class);

        // Simulate the row being created and index being assigned before adding
        when(mockTable.createNewRow()).thenReturn(mockRow);
        when(mockTable.getRowsCount()).thenReturn(5);

        command = new AddRowCommand(mockTable);
    }

    @Test
    void testExecute_addsRowAtCorrectIndex() {
        command.execute();
        verify(mockTable).addRow(5, mockRow);
    }

    @Test
    void testUndo_deletesRowAtCorrectIndex() {
        command.undo();
        verify(mockTable).deleteRow(5);
    }
}
