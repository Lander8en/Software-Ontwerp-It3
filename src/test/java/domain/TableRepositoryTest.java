package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.observers.TableNameChangeObserver;
import domain.observers.TableRemovalObserver;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class TableRepositoryTest {

    private TableRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new TableRepository();
    }

    @Test
    public void testCreateNewTableAddsTableWithUniqueName() {
        Table table1 = repository.createNewTable();
        repository.addTable(0, table1);

        Table table2 = repository.createNewTable();
        repository.addTable(1, table2);

        List<Table> tables = repository.getTables();
        assertEquals(2, tables.size());
        assertNotEquals(tables.get(0).getName(), tables.get(1).getName());
        assertTrue(tables.get(0).getName().startsWith("Table"));
        assertTrue(tables.get(1).getName().startsWith("Table"));
    }

    @Test
    public void testUpdateTableNameWithValidNameChangesName() {
        Table table = repository.createNewTable();
        repository.addTable(0, table);

        repository.updateTableName(0, "NewTableName");

        assertEquals("NewTableName", repository.getTables().get(0).getName());
    }

    @Test
    public void testUpdateTableNameWithInvalidNameDoesNotChange() {
        Table table1 = repository.createNewTable();
        repository.addTable(0, table1);
        Table table2 = repository.createNewTable();
        repository.addTable(1, table2);

        String originalName = table2.getName();
        repository.updateTableName(1, table1.getName()); // Duplicate name
        assertEquals(originalName, repository.getTables().get(1).getName());
    }

    @Test
    public void testIsTableNameValid() {
        Table table = repository.createNewTable();
        repository.addTable(0, table);

        assertFalse(repository.isTableNameValid(null, table));
        assertFalse(repository.isTableNameValid("", table));
        assertFalse(repository.isTableNameValid("   ", table));
        assertFalse(repository.isTableNameValid(table.getName(), null));
        assertTrue(repository.isTableNameValid("UniqueName", table));
    }

    @Test
    public void testRemoveTableRemovesCorrectly() {
        Table table = repository.createNewTable();
        repository.addTable(0, table);

        repository.remove(table);

        assertEquals(0, repository.getTablesCount());
    }

    @Test
    public void testRemoveTableNotifiesObservers() {
        Table table = repository.createNewTable();
        repository.addTable(0, table);

        AtomicBoolean wasNotified = new AtomicBoolean(false);
        repository.addRemovalObserver(removed -> {
            if (removed.getName().equals(table.getName())) {
                wasNotified.set(true);
            }
        });

        repository.remove(table);

        assertTrue(wasNotified.get());
    }

    @Test
    public void testUpdateTableNameNotifiesObservers() {
        Table table = repository.createNewTable();
        repository.addTable(0, table);

        AtomicBoolean wasNotified = new AtomicBoolean(false);
        repository.addNameChangeObserver(updated -> {
            if (updated.getName().equals("ChangedName")) {
                wasNotified.set(true);
            }
        });

        repository.updateTableName(0, "ChangedName");

        assertTrue(wasNotified.get());
    }

    @Test
    public void testAddNullTableDoesNotThrow() {
        assertDoesNotThrow(() -> repository.addTable(0, null));
        assertEquals(0, repository.getTablesCount());
    }

    @Test
    public void testRemoveNullDoesNothing() {
        assertDoesNotThrow(() -> repository.remove(null));
    }

    @Test
    public void testAddAndGetDeepCopyTable() {
        Table table = repository.createNewTable();
        repository.addTable(0, table);

        Table copy = repository.getDeepCopyTable(0);
        assertNotSame(copy, table);
        assertEquals(copy.getName(), table.getName());
    }

    @Test
    public void testAddRemovalObserverWithNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> repository.addRemovalObserver(null));
    }

    @Test
    public void testAddNameChangeObserverWithNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> repository.addNameChangeObserver(null));
    }

    @Test
    public void testRemoveRemovalObserver() {
        TableRemovalObserver observer = t -> {
        };
        repository.addRemovalObserver(observer);
        repository.removeRemovalObserver(observer);

        // Not directly observable, but we can at least assert no exception is thrown
        assertDoesNotThrow(() -> repository.removeRemovalObserver(observer));
    }

    @Test
    public void testRemoveChangeObserver() {
        TableNameChangeObserver observer = t -> {
        };
        repository.addNameChangeObserver(observer);
        repository.removeChangeObserver(observer);

        // Again, ensure no exception thrown
        assertDoesNotThrow(() -> repository.removeChangeObserver(observer));
    }

    @Test
    public void testGetTableIndexAndIndexOf() {
        Table table = repository.createNewTable();
        repository.addTable(0, table);

        assertEquals(0, repository.getTableIndex(table));
        assertEquals(0, repository.indexOf(table));
    }

    @Test
    public void testGetTableName() {
        Table table = repository.createNewTable();
        repository.addTable(0, table);

        assertEquals(table.getName(), repository.getTableName(0));
    }
}
