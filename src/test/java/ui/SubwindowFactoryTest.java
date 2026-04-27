package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.controllers.TableController;
import ui.controllers.TableRepoController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubwindowFactoryTest {

    private SubwindowFactory factory;
    private TableRepoController mockRepoController;
    private TableController mockTableController;

    @BeforeEach
    void setUp() {
        factory = new SubwindowFactory();
        mockRepoController = mock(TableRepoController.class);

        mockTableController = mock(TableController.class);
        when(mockTableController.getTableName()).thenReturn("MockTable");
    }

    @Test
    void createNewTableSubwindow_ReturnsTableSubwindowWithCorrectPositionAndSize() {
        TablesSubwindow subwindow = factory.createNewTableSubwindow(mockRepoController);

        assertNotNull(subwindow);
        assertEquals(50, subwindow.getX());
        assertEquals(50, subwindow.getY());
        assertEquals(300, subwindow.width);
        assertEquals(200, subwindow.height);
        assertEquals("Tables", subwindow.title);
    }

    @Test
    void createNewTableDesignSubwindow_ReturnsDesignSubwindowWithCorrectTitleAndPosition() {
        TableDesignSubwindow subwindow = factory.createNewTableDesignSubwindow(mockTableController);

        assertNotNull(subwindow);
        assertEquals(50, subwindow.getX());
        assertEquals(50, subwindow.getY());
        assertEquals(610, subwindow.width); // width differs from other windows
        assertEquals(200, subwindow.height);
        assertEquals("Table Design - MockTable", subwindow.title);
    }

    @Test
    void createNewTableRowsSubwindow_ReturnsRowsSubwindowWithCorrectTitleAndPosition() {
        TableRowsSubwindow subwindow = factory.createNewTableRowsSubwindow(mockTableController);

        assertNotNull(subwindow);
        assertEquals(50, subwindow.getX());
        assertEquals(50, subwindow.getY());
        assertEquals(610, subwindow.width);
        assertEquals(200, subwindow.height);
        assertEquals("Table Rows - MockTable", subwindow.title);
    }

    @Test
    void createNewFormSubwindow_ReturnsFormSubwindowWithCorrectTitleAndPosition() {
        FormSubwindow subwindow = factory.createNewFormSubwindow(mockTableController);

        assertNotNull(subwindow);
        assertEquals(50, subwindow.getX());
        assertEquals(50, subwindow.getY());
        assertEquals(350, subwindow.width);
        assertEquals(200, subwindow.height);
        assertEquals("MockTable - Row 1", subwindow.title);
    }

    @Test
    void createdCount_IncrementsCorrectlyAcrossMultipleCreations() {
        TablesSubwindow sub1 = factory.createNewTableSubwindow(mockRepoController); // offset = 0
        TableDesignSubwindow sub2 = factory.createNewTableDesignSubwindow(mockTableController); // offset = 20
        TableRowsSubwindow sub3 = factory.createNewTableRowsSubwindow(mockTableController); // offset = 40
        FormSubwindow sub4 = factory.createNewFormSubwindow(mockTableController); // offset = 60

        assertEquals(50, sub1.getX());
        assertEquals(70, sub2.getX());
        assertEquals(90, sub3.getX());
        assertEquals(110, sub4.getX());

        assertEquals(50, sub1.getY());
        assertEquals(70, sub2.getY());
        assertEquals(90, sub3.getY());
        assertEquals(110, sub4.getY());
    }
}
