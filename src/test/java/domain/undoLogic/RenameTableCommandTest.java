package domain.undoLogic;

import domain.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RenameTableCommandTest {

    private TableRepository mockRepo;
    private RenameTableCommand command;

    @BeforeEach
    void setUp() {
        mockRepo = mock(TableRepository.class);
        when(mockRepo.getTableName(0)).thenReturn("OldTableName");
        command = new RenameTableCommand(mockRepo, 0, "NewTableName");
    }

    @Test
    void testExecute_updatesTableNameToNewName() {
        command.execute();
        verify(mockRepo).updateTableName(0, "NewTableName");
    }

    @Test
    void testUndo_restoresOldTableName() {
        command.undo();
        verify(mockRepo).updateTableName(0, "OldTableName");
    }

    @Test
    void testConstructorThrowsOnNullRepository() {
        assertThrows(NullPointerException.class, () -> {
            new RenameTableCommand(null, 0, "NewTable");
        }, "Expected constructor to throw NullPointerException for null repo");
    }

    @Test
    void testConstructorThrowsOnNullNewName() {
        assertThrows(NullPointerException.class, () -> {
            new RenameTableCommand(mockRepo, 0, null);
        }, "Expected constructor to throw NullPointerException for null new name");
    }
}
