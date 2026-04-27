package ui.controllers;

import domain.Table;
import domain.TableRepository;
import domain.observers.TableNameChangeObserver;
import domain.observers.TableRemovalObserver;
import domain.undoLogic.UndoManager;
import domain.undoLogic.AddTableCommand;
import domain.undoLogic.DeleteTableCommand;
import domain.undoLogic.RenameTableCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TableRepoControllerTest {

    private TableRepository mockRepo;
    private TableRepoController controller;

    @BeforeEach
    public void setUp() {
        mockRepo = mock(TableRepository.class);

        // Hacky but effective: inject mock repo via reflection
        controller = new TableRepoController();
        try {
            var field = TableRepoController.class.getDeclaredField("tableRepository");
            field.setAccessible(true);
            field.set(controller, mockRepo);
        } catch (Exception e) {
            fail("Reflection injection failed: " + e.getMessage());
        }
    }

    @Test
    public void testTablesRequestDelegatesToRepo() {
        List<Table> mockTables = Arrays.asList(mock(Table.class), mock(Table.class));
        when(mockRepo.getTables()).thenReturn(mockTables);

        List<Table> result = controller.tablesRequest();

        assertEquals(mockTables, result);
        verify(mockRepo).getTables();
    }

    @Test
    public void testGetTablesCountDelegatesToRepo() {
        when(mockRepo.getTablesCount()).thenReturn(3);
        assertEquals(3, controller.getTablesCount());
    }

    @Test
    public void testIsTableNameValidDelegatesToRepo() {
        Table mockTable = mock(Table.class);
        when(mockRepo.isTableNameValid("Test", mockTable)).thenReturn(true);

        assertTrue(controller.isTableNameValid("Test", mockTable));
    }

    @Test
    public void testGetTableIndexDelegatesToRepo() {
        Table mockTable = mock(Table.class);
        when(mockRepo.getTableIndex(mockTable)).thenReturn(1);

        assertEquals(1, controller.getTableIndex(mockTable));
    }

    @Test
    public void testGetTableNameDelegatesToRepo() {
        when(mockRepo.getTableName(0)).thenReturn("MyTable");

        assertEquals("MyTable", controller.getTableName(0));
    }

    @Test
    public void testAddTableRemovalObserverDelegates() {
        TableRemovalObserver observer = mock(TableRemovalObserver.class);
        controller.addTableRemovalObserver(observer);

        verify(mockRepo).addRemovalObserver(observer);
    }

    @Test
    public void testAddTableNameChangeObserverDelegates() {
        TableNameChangeObserver observer = mock(TableNameChangeObserver.class);
        controller.addTableNameChangeObserver(observer);

        verify(mockRepo).addNameChangeObserver(observer);
    }

    @Test
    public void testHandleCreateNewTableRequestExecutesCommand() {
        try (MockedStatic<UndoManager> mocked = mockStatic(UndoManager.class)) {
            UndoManager mockUndoManager = mock(UndoManager.class);
            mocked.when(UndoManager::getInstance).thenReturn(mockUndoManager);

            controller.handleCreateNewTableRequest();

            verify(mockUndoManager).execute(any(AddTableCommand.class));
        }
    }

    @Test
    public void testHandleDeleteTableRequestExecutesCommandWithCorrectArgs() {
        Table table = mock(Table.class);
        when(mockRepo.getDeepCopyTable(1)).thenReturn(table);

        try (MockedStatic<UndoManager> mocked = mockStatic(UndoManager.class)) {
            UndoManager mockUndoManager = mock(UndoManager.class);
            mocked.when(UndoManager::getInstance).thenReturn(mockUndoManager);

            controller.handleDeleteTableRequest(1);

            verify(mockRepo).getDeepCopyTable(1);
            verify(mockUndoManager).execute(any(DeleteTableCommand.class));
        }
    }

    @Test
    public void testRenameExecutesCommand() {
        try (MockedStatic<UndoManager> mocked = mockStatic(UndoManager.class)) {
            UndoManager mockUndoManager = mock(UndoManager.class);
            mocked.when(UndoManager::getInstance).thenReturn(mockUndoManager);

            controller.rename(0, "NewName");

            verify(mockUndoManager).execute(any(RenameTableCommand.class));
        }
    }
}
