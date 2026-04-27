package domain;

import domain.observers.ColumnAttributesObserver;
import domain.observers.ColumnNameChangeObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ColumnRepositoryTest {

    private ColumnRepository repository;

    @BeforeEach
    public void setup() {
        repository = new ColumnRepository();
    }

    @Test
    public void testAddAndRemoveObserver() {
        ColumnAttributesObserver observer = mock(ColumnAttributesObserver.class);
        repository.addObserver(observer);
        repository.removeObserver(observer);
        Column col = repository.createNewColumn();
        repository.addColumn(0, col);
        repository.setType(0, ColumnType.INTEGER);
        verify(observer, never()).onColumnChanged(anyInt());
    }

    @Test
    public void testRenameColumnAndNotify() {
        Column col = repository.createNewColumn();
        repository.addColumn(0, col);
        ColumnNameChangeObserver observer = mock(ColumnNameChangeObserver.class);
        repository.addNameChangeObserver(observer);

        repository.rename(0, "RenamedColumn");

        assertEquals("RenamedColumn", repository.getName(0));
        verify(observer).onColumnNameChange(0);
    }

    @Test
    public void testRemoveColumnAndNotify() {
        Column col = repository.createNewColumn();
        repository.addColumn(0, col);
        ColumnNameChangeObserver observer = mock(ColumnNameChangeObserver.class);
        repository.addNameChangeObserver(observer);

        repository.remove(0);

        assertEquals(0, repository.getColumnsCount());
        verify(observer).onColumnNameChange(0);
    }

    @Test
    public void testGetType() {
        Column col = repository.createNewColumn();
        col.setType(ColumnType.INTEGER);
        repository.addColumn(0, col);
        assertEquals(ColumnType.INTEGER, repository.getType(0));
    }

    @Test
    public void testAllowsBlanks() {
        Column col = repository.createNewColumn();
        col.setBlanksAllowed(false);
        repository.addColumn(0, col);
        assertFalse(repository.allowsBlanks(0));
    }

    @Test
    public void testSetTypeUpdatesColumnAndNotifiesObserver() {
        Column col = repository.createNewColumn();
        repository.addColumn(0, col);
        ColumnAttributesObserver observer = mock(ColumnAttributesObserver.class);
        repository.addObserver(observer);

        repository.setType(0, ColumnType.INTEGER);

        assertEquals(ColumnType.INTEGER, repository.getType(0));
        verify(observer).onColumnChanged(0);
    }

    @Test
    public void testSetDefaultValueNotifiesBothObservers() {
        Column col = repository.createNewColumn();
        repository.addColumn(0, col);
        ColumnAttributesObserver observer = mock(ColumnAttributesObserver.class);
        repository.addObserver(observer);

        repository.setDefaultValue(0, "42");

        assertEquals("42", repository.getDefaultValue(0));
        verify(observer).onColumnChanged(0);
        verify(observer).onDefaultValueChanged(0);
    }

    @Test
    public void testToggleBlanksAllowedTogglesValueAndNotifiesObserver() {
        Column col = repository.createNewColumn();
        col.setBlanksAllowed(false);
        repository.addColumn(0, col);
        ColumnAttributesObserver observer = mock(ColumnAttributesObserver.class);
        repository.addObserver(observer);

        repository.toggleBlanksAllowed(0);

        assertTrue(repository.allowsBlanks(0));
        verify(observer).onColumnChanged(0);
    }

    @Test
    public void testAddObserver_Null_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> repository.addObserver(null));
    }

    @Test
    public void testRemoveObserver_Null_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> repository.removeObserver(null));
    }

    @Test
    public void testCreateMultipleColumnsGeneratesUniqueNames() {
        Column col1 = repository.createNewColumn();
        repository.addColumn(0, col1);
        Column col2 = repository.createNewColumn();
        repository.addColumn(1, col2);
        Column col3 = repository.createNewColumn();
        repository.addColumn(2, col3);

        assertNotEquals(col1.getName(), col2.getName());
        assertNotEquals(col2.getName(), col3.getName());
        assertNotEquals(col1.getName(), col3.getName());
    }

    @Test
    public void testGetColumnsReturnsDeepCopy() {
        Column original = repository.createNewColumn();
        original.setType(ColumnType.STRING);
        original.setDefaultValue("default");
        original.setBlanksAllowed(true);
        repository.addColumn(0, original);

        Column copy = repository.getColumns().get(0);
        assertNotSame(original, copy);
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getType(), copy.getType());
        assertEquals(original.getDefaultValue(), copy.getDefaultValue());
        assertEquals(original.isBlanksAllowed(), copy.isBlanksAllowed());
    }

    @Test
    public void testRemoveNameChangeObserver_doesNotNotifyAfterRemoval() {
        Column col = repository.createNewColumn();
        repository.addColumn(0, col);
        ColumnNameChangeObserver observer = mock(ColumnNameChangeObserver.class);
        repository.addNameChangeObserver(observer);
        repository.removeNameChangeObserver(observer);

        repository.rename(0, "NewName");

        verify(observer, never()).onColumnNameChange(0);
    }

    @Test
    public void testIndexOf_existingColumn() {
        Column col = repository.createNewColumn();
        repository.addColumn(0, col);
        assertEquals(0, repository.indexOf(col));
    }

    @Test
    public void testIndexOf_nonExistingColumnReturnsMinusOne() {
        Column col = new Column("NotInRepo");
        assertEquals(-1, repository.indexOf(col));
    }

    @Test
    public void testGetName_returnsCorrectName() {
        Column col = new Column("OriginalName");
        repository.addColumn(0, col);
        assertEquals("OriginalName", repository.getName(0));
    }

    @Test
    public void testGetColumnsCount() {
        assertEquals(0, repository.getColumnsCount());
        Column col = repository.createNewColumn();
        repository.addColumn(0, col);
        assertEquals(1, repository.getColumnsCount());
    }

    @Test
    public void testToggleBlanksAllowed_TrueToFalse() {
        Column col = repository.createNewColumn();
        col.setBlanksAllowed(true); // explicitly start true
        repository.addColumn(0, col);

        repository.toggleBlanksAllowed(0);

        assertFalse(repository.allowsBlanks(0));
    }

    @Test
    public void testToggleBlanksAllowed_FalseToTrue() {
        Column col = repository.createNewColumn();
        col.setBlanksAllowed(false); // explicitly start false
        repository.addColumn(0, col);

        repository.toggleBlanksAllowed(0);

        assertTrue(repository.allowsBlanks(0));
    }

}
