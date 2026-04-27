package ui.editors;

import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.controllers.TableRepoController;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TableNameAccessTest {

    private TableRepoController mockController;
    private TableNameAccess nameAccess;

    @BeforeEach
    void setUp() {
        mockController = mock(TableRepoController.class);
        nameAccess = new TableNameAccess(mockController);
    }

    @Test
    void getName_ReturnsNameFromController() {
        when(mockController.getTableName(1)).thenReturn("ExampleTable");

        String result = nameAccess.getName(1);

        assertEquals("ExampleTable", result);
        verify(mockController).getTableName(1);
    }

    @Test
    void setName_DelegatesToController() {
        nameAccess.setName(2, "NewTableName");

        verify(mockController).rename(2, "NewTableName");
    }

    @Test
    void getAll_ReturnsTablesFromController() {
        List<Table> mockTables = Arrays.asList(mock(Table.class), mock(Table.class));
        when(mockController.tablesRequest()).thenReturn(mockTables);

        List<Table> result = nameAccess.getAll();

        assertEquals(mockTables, result);
        verify(mockController).tablesRequest();
    }
}
