package domain.undoLogic;

import domain.Column;
import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteColumnCommandTest {

    private Table mockTable;
    private Column mockColumn;
    private DeleteColumnCommand command;

    @BeforeEach
    void setUp() {
        mockTable = mock(Table.class);
        mockColumn = mock(Column.class);

        // Simulate that table.getColumn(1) returns mockColumn
        when(mockTable.getColumn(1)).thenReturn(mockColumn);

        command = new DeleteColumnCommand(mockTable, 1);
    }

    @Test
    void testExecute_removesColumnAtGivenIndex() {
        command.execute();
        verify(mockTable).removeColumn(1);
    }

    @Test
    void testUndo_restoresColumnAtGivenIndex() {
        command.undo();
        verify(mockTable).addColumn(1, mockColumn);
    }

    @Test
    void testConstructorThrowsOnNullTable() {
        assertThrows(NullPointerException.class, () -> {
            new DeleteColumnCommand(null, 1);
        }, "Expected constructor to throw NullPointerException for null table");
    }
}
