package ui.editors;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import domain.Column;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.controllers.TableController;

import java.util.Arrays;
import java.util.List;

public class ColumnNameAccessTest {

    private TableController mockController;
    private ColumnNameAccess columnNameAccess;

    @BeforeEach
    public void setUp() {
        mockController = mock(TableController.class);
        columnNameAccess = new ColumnNameAccess(mockController);
    }

    @Test
    public void testGetNameReturnsColumnName() {
        // Arrange
        when(mockController.getColumnName(1)).thenReturn("TestColumn");

        // Act
        String result = columnNameAccess.getName(1);

        // Assert
        assertEquals("TestColumn", result);
        verify(mockController).getColumnName(1);
    }

    @Test
    public void testSetNameCallsControllerRename() {
        // Act
        columnNameAccess.setName(2, "RenamedColumn");

        // Assert
        verify(mockController).renameColumn(2, "RenamedColumn");
    }

    @Test
    public void testGetAllReturnsColumnsFromController() {
        // Arrange
        List<Column> mockColumns = Arrays.asList(
                mock(Column.class),
                mock(Column.class));
        when(mockController.columnsRequest()).thenReturn(mockColumns);

        // Act
        List<Column> result = columnNameAccess.getAll();

        // Assert
        assertEquals(mockColumns, result);
        verify(mockController).columnsRequest();
    }
}
