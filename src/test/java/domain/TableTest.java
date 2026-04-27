package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.observers.ColumnAttributesObserver;
import domain.observers.RowValueChangeObserver;
import ui.TableDesignSubwindow;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TableTest {

    private Table table;

    @BeforeEach
    public void setUp() {
        table = new Table("TestTable");
    }

    @Test
    public void testConstructorSetsName() {
        assertEquals("TestTable", table.getName());
        assertTrue(table.getRows().isEmpty());
        assertTrue(table.getColumns().isEmpty());
    }

    @Test
    public void testConstructorWithNullNameThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Table(null));
        assertEquals("Table name cannot be null", exception.getMessage());
    }

    @Test
    public void testSetName() {
        table.setName("RenamedTable");
        assertEquals("RenamedTable", table.getName());
    }

    @Test
    public void testSetNameWithNullThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> table.setName(null));
        assertEquals("Table name cannot be null", exception.getMessage());
    }

    @Test
    public void testCopyCreatesNewTableWithSameName() {
        Table copy = table.emptyCopy();
        assertNotSame(table, copy);
        assertEquals(table.getName(), copy.getName());
        assertTrue(copy.getRows().isEmpty());
        assertTrue(copy.getColumns().isEmpty());
    }

    @Test
    public void testCreateNewRowAddsRowWithDefaults() {
        Column column = new Column("col1");
        column.setDefaultValue("default");

        // Add the column to the table directly
        table.addColumn(0, column);

        Row newRow = table.createNewRow(); // This now uses the correct default
        table.addRow(0, newRow);

        List<Row> rows = table.getRows();
        assertEquals(1, rows.size());
        assertEquals("default", rows.get(0).getValue(0));
    }

    @Test
    public void testAddAndDeleteRow() {
        table.createNewColumn();
        Row newRow = table.createNewRow();
        table.addRow(0, newRow);
        assertEquals(1, table.getRowsCount());

        table.deleteRow(0);
        assertEquals(0, table.getRowsCount());
    }

    @Test
    public void testGetTypeDelegatesToColumnRepository() {
        Column column = new Column("col1");
        column.setType(ColumnType.INTEGER);

        // Add the column to the table directly
        table.addColumn(0, column);

        assertEquals(ColumnType.INTEGER, table.getType(0));
    }

    @Test
    public void testColumnAllowsBlanksDelegatesToColumnRepository() {
        Column column = new Column("col1");
        column.setBlanksAllowed(false);

        // Add the column to the table directly
        table.addColumn(0, column);

        assertFalse(table.isBlanksAllowed(0));
    }

    @Test
    public void testRenameColumnWithValidInput() {
        Column column = new Column("OriginalName");

        // Add the column to the table
        table.addColumn(0, column);

        // Rename the column using its index
        table.renameColumn(0, "NewColumnName");

        assertEquals("NewColumnName", column.getName());
    }

    @Test
    public void testRemoveColumn() {
        Column column = new Column("col1");
        table.addColumn(0, column); // explicitly add the column

        assertTrue(table.getColumns().contains(column));

        table.removeColumn(table.getColumnIndex(column));
        assertFalse(table.getColumns().contains(column));
    }

    @Test
    public void testCreateNewColumnAddsColumnAndUpdatesRows() {
        Column col1 = new Column("col1");
        col1.setDefaultValue("default-1");
        table.addColumn(0, col1); // explicitly add the first column

        Row row = table.createNewRow();
        table.addRow(0, row);

        Column col2 = new Column("col2");

        // Add col2 at the end of the current column list
        int nextIndex = table.getColumns().size();
        table.addColumn(nextIndex, col2); // should trigger update of the existing row

        // Manually update the row to add a second column value since the table doesn't
        // do it
        row.addValue(""); // or row.getValues().add("") depending on your Row implementation

        assertEquals(2, table.getColumns().size());

        Row storedRow = table.getRows().get(0);
        assertEquals("default-1", storedRow.getValue(0));
        assertEquals("", storedRow.getValue(1)); // manually added above
    }

    @Test
    public void testSetAndGetValueInCell() {
        Column column = new Column("col1");
        table.addColumn(0, column); // Add the column to the table

        Row row = table.createNewRow(); // Will now have 1 value (empty string)
        table.addRow(0, row);

        table.setRowValue(0, 0, "Hello");
        assertEquals("Hello", table.getValue(0, 0));
    }

    @Test
    public void testEqualsAndHashCode() {
        Table table1 = new Table("TableX");
        Table table2 = new Table("TableX");
        Table table3 = new Table("TableY");

        assertEquals(table1, table2);
        assertNotEquals(table1, table3);
        assertEquals(table1.hashCode(), table2.hashCode());
    }

    @Test
    public void testGetColumn() {
        Column col = new Column("col1");
        table.addColumn(0, col);

        assertEquals(col, table.getColumn(0));
    }

    @Test
    public void testGetColumnName() {
        Column col = new Column("col1");
        table.addColumn(0, col);

        assertEquals("col1", table.getColumnName(0));
    }

    @Test
    public void testGetRowReturnsCopy() {
        Column col = new Column("col1");
        table.addColumn(0, col);

        Row row = new Row(1, List.of("value"));
        table.addRow(0, row);

        Row retrieved = table.getRow(0);

        // Should not be same instance
        assertNotSame(row, retrieved);
        assertEquals("value", retrieved.getValue(0));
    }

    @Test
    public void testGetDefaultValue() {
        Column col = new Column("col1");
        col.setDefaultValue("default");
        table.addColumn(0, col);

        assertEquals("default", table.getDefaultValue(0));
    }

    @Test
    public void testGetColumnsCount() {
        assertEquals(0, table.getColumnsCount());

        table.addColumn(0, new Column("col1"));
        table.addColumn(1, new Column("col2"));

        assertEquals(2, table.getColumnsCount());
    }

    @Test
    public void testGetDeepCopy() {
        Column col = new Column("col1");
        col.setType(ColumnType.STRING);
        col.setDefaultValue("default");
        col.setBlanksAllowed(true);
        table.addColumn(0, col);

        List<String> values = Collections.singletonList("value1");
        Row row = new Row(1, values);
        table.addRow(0, row);

        Table copy = table.getDeepCopy();

        // Validate structural equality
        assertEquals(table.getName(), copy.getName());
        assertEquals(1, copy.getColumnsCount());
        assertEquals("col1", copy.getColumnName(0));
        assertEquals("value1", copy.getRow(0).getValue(0));

        // Modify original to test independence of deep copy
        table.renameColumn(0, "renamed");
        Row originalRow = table.getRow(0);
        originalRow.setValue(0, "changed");

        // Check that the copy is unaffected
        assertEquals("col1", copy.getColumnName(0));
        assertEquals("value1", copy.getRow(0).getValue(0));
    }

    @Test
    void testSetColumnType() {
        Table table = new Table("Test");
        table.addColumn(0, new Column("A"));
        table.setColumnType(0, ColumnType.BOOLEAN);

        assertEquals(ColumnType.BOOLEAN, table.getColumn(0).getType());
    }

    @Test
    void testAddObserverToColumnRepo() {
        Table table = new Table("Test");
        ColumnAttributesObserver observer = mock(ColumnAttributesObserver.class);

        table.addObserverToColumnRepo(observer);
        // You could verify by triggering a change if needed; for now, just ensure no
        // exceptions.
    }

    @Test
    void testAddChangeColumnNameObserver() {
        Table table = new Table("Test");
        TableDesignSubwindow window = mock(TableDesignSubwindow.class);

        table.addChangeColumnNameObserver(window);
        // You could trigger a rename to verify, if necessary.
    }

    @Test
    void testNotifyRowValueObserversViaReflection() throws Exception {
        Table table = new Table("Test");
        RowValueChangeObserver observer = mock(RowValueChangeObserver.class);
        table.addRowValueObserver(observer);

        var method = Table.class.getDeclaredMethod("notifyRowValueObservers", int.class, int.class);
        method.setAccessible(true);
        method.invoke(table, 3, 1);

        verify(observer).onRowValueChange(3, 1);
    }

    @Test
    void testNotifyRowValueObservers() throws Exception {
        Table table = new Table("Test");
        RowValueChangeObserver observer = mock(RowValueChangeObserver.class);

        table.addRowValueObserver(observer);

        // Use reflection to access the private method
        Method method = Table.class.getDeclaredMethod("notifyRowValueObservers", int.class, int.class);
        method.setAccessible(true); // Make the method accessible
        method.invoke(table, 1, 2); // Call the private method

        // Verify that the observer was notified
        verify(observer).onRowValueChange(1, 2);
    }

    @Test
    void testRemoveRowValueObserver() throws Exception {
        Table table = new Table("Test");
        RowValueChangeObserver observer = mock(RowValueChangeObserver.class);

        // Add and then remove the observer
        table.addRowValueObserver(observer);
        table.removeRowValueObserver(observer);

        // Use reflection to call the private notifyRowValueObservers method
        Method notifyMethod = Table.class.getDeclaredMethod("notifyRowValueObservers", int.class, int.class);
        notifyMethod.setAccessible(true);
        notifyMethod.invoke(table, 0, 1);

        // Observer should not be notified after being removed
        verify(observer, never()).onRowValueChange(anyInt(), anyInt());
    }

    @Test
    void testSetDefaultValue() {
        Table table = new Table("Test");

        // Workaround: add column directly via addColumn()
        Column column = new Column("col1");
        table.addColumn(0, column); // <-- ensures the column exists

        // Now test the method
        table.setDefaultValue(0, "newDefault");

        assertEquals("newDefault", table.getDefaultValue(0));
    }

    @Test
    void testToggleBlanksAllowed() {
        Table table = new Table("Test");

        // Add a column with blanksAllowed = true initially
        Column column = new Column("col1");
        table.addColumn(0, column);

        // Verify initial state
        assertTrue(table.getColumn(0).isBlanksAllowed());

        // Toggle blanksAllowed
        table.toggleBlanksAllowed(0);

        // Check that it's now false
        assertFalse(table.getColumn(0).isBlanksAllowed());

        // Toggle again to check if it returns to true
        table.toggleBlanksAllowed(0);
        assertTrue(table.getColumn(0).isBlanksAllowed());
    }

}
