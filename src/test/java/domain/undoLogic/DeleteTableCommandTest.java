package domain.undoLogic;

import domain.Table;
import domain.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteTableCommandTest {

    private TableRepository mockRepo;
    private Table mockTable;
    private DeleteTableCommand command;

    @BeforeEach
    void setUp() {
        mockRepo = mock(TableRepository.class);
        mockTable = mock(Table.class);
        command = new DeleteTableCommand(mockRepo, mockTable, 1);
    }

    @Test
    void testExecute_removesTableFromRepository() {
        command.execute();
        verify(mockRepo).remove(mockTable);
    }

    @Test
    void testUndo_addsTableBackToRepositoryAtCorrectIndex() {
        command.undo();
        verify(mockRepo).addTable(1, mockTable);
    }

    @Test
    void testConstructorThrowsOnNullRepo() {
        assertThrows(NullPointerException.class, () -> {
            new DeleteTableCommand(null, mockTable, 1);
        }, "Expected constructor to throw NullPointerException for null repository");
    }

    @Test
    void testConstructorThrowsOnNullTable() {
        assertThrows(NullPointerException.class, () -> {
            new DeleteTableCommand(mockRepo, null, 1);
        }, "Expected constructor to throw NullPointerException for null table");
    }
}
