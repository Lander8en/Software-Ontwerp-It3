package domain.undoLogic;

import domain.Table;
import domain.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.controllers.TableController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AddTableCommandTest {

    private TableRepository mockRepo;
    private Table mockTable;
    private List<TableController> controllers;
    private AddTableCommand command;

    @BeforeEach
    void setUp() {
        mockRepo = mock(TableRepository.class);
        mockTable = mock(Table.class);
        controllers = spy(new ArrayList<>());

        when(mockRepo.getTablesCount()).thenReturn(2);
        when(mockRepo.createNewTable()).thenReturn(mockTable);

        command = new AddTableCommand(mockRepo, controllers);
    }

    @Test
    void testExecute_addsTableToRepoAndControllerList() {
        command.execute();

        verify(mockRepo).addTable(2, mockTable);
        assertEquals(1, controllers.size());
    }

    @Test
    void testUndo_removesTableAndController() {
        when(mockRepo.getTablesCount()).thenReturn(0);
        when(mockRepo.createNewTable()).thenReturn(mockTable);
        command = new AddTableCommand(mockRepo, controllers);
        command.execute();
        assertEquals(1, controllers.size());
        command.undo();

        verify(mockRepo).remove(mockTable);
        assertTrue(controllers.isEmpty());
    }

}
