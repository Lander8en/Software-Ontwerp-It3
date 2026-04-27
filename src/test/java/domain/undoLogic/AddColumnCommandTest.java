package domain.undoLogic;

import domain.Column;
import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class AddColumnCommandTest {

    private Table mockTable;
    private Column mockColumn;
    private AddColumnCommand command;

    @BeforeEach
    void setUp() {
        mockTable = mock(Table.class);
        mockColumn = mock(Column.class);

        // Assume createNewColumn returns the new column
        when(mockTable.createNewColumn()).thenReturn(mockColumn);
        // Assume current columns count is 3 before adding
        when(mockTable.getColumnsCount()).thenReturn(3);

        // Now create the command
        command = new AddColumnCommand(mockTable);
    }

    @Test
    void testExecute_addsColumnAtCorrectIndex() {
        command.execute();
        verify(mockTable).addColumn(3, mockColumn);
    }

    @Test
    void testUndo_removesColumnAtCorrectIndex() {
        command.undo();
        verify(mockTable).removeColumn(3);
    }
}
