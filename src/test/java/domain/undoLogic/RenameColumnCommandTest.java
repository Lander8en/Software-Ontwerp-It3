package domain.undoLogic;

import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RenameColumnCommandTest {

    private Table mockTable;
    private RenameColumnCommand command;

    @BeforeEach
    void setUp() {
        mockTable = mock(Table.class);
        when(mockTable.getColumnName(0)).thenReturn("OldName");
        command = new RenameColumnCommand(mockTable, 0, "NewName");
    }

    @Test
    void testExecute_renamesColumnToNewName() {
        command.execute();
        verify(mockTable).renameColumn(0, "NewName");
    }

    @Test
    void testUndo_renamesColumnBackToOldName() {
        command.undo();
        verify(mockTable).renameColumn(0, "OldName");
    }

    @Test
    void testConstructorThrowsOnNullTable() {
        assertThrows(NullPointerException.class, () -> {
            new RenameColumnCommand(null, 0, "NewName");
        }, "Expected constructor to throw NullPointerException for null table");
    }

    @Test
    void testConstructorThrowsOnNullName() {
        assertThrows(NullPointerException.class, () -> {
            new RenameColumnCommand(mockTable, 0, null);
        }, "Expected constructor to throw NullPointerException for null new name");
    }
}
