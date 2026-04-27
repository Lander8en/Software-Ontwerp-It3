package ui.controllers;

import java.util.ArrayList;
import java.util.List;

import domain.Table;
import domain.TableRepository;
import domain.observers.TableNameChangeObserver;
import domain.observers.TableRemovalObserver;
import domain.undoLogic.AddTableCommand;
import domain.undoLogic.DeleteTableCommand;
import domain.undoLogic.RenameTableCommand;
import domain.undoLogic.UndoManager;

/**
 * Controller for managing the table repository and its associated TableControllers.
 * Handles operations such as creating, renaming, and deleting tables,
 * as well as managing observers and providing access to the table list.
 */
public class TableRepoController {

    private final TableRepository tableRepository = new TableRepository();
    private final List<TableController> controllers = new ArrayList<>();

    // ===== Observer Registration =====

    /**
     * Registers an observer that will be notified when a table is removed.
     */
    public void addTableRemovalObserver(TableRemovalObserver observer) {
        tableRepository.addRemovalObserver(observer);
    }

    /**
     * Registers an observer that will be notified when a table name changes.
     */
    public void addTableNameChangeObserver(TableNameChangeObserver observer) {
        tableRepository.addNameChangeObserver(observer);
    }

    // ===== Table Access =====

    /**
     * Returns the TableController associated with the table at the given index.
     */
    public TableController getTableController(int index) {
        return controllers.get(index);
    }

    /**
     * Returns a list of deep-copied tables.
     */
    public List<Table> tablesRequest() {
        return tableRepository.getTables();
    }

    /**
     * Returns the number of tables in the repository.
     */
    public int tablesCount() {
        return tableRepository.getTablesCount();
    }

    /**
     * Returns whether a given name is valid (non-empty and unique) among all tables,
     * excluding the provided table.
     */
    public boolean isTableNameValid(String name, Table excludeTable) {
        return tableRepository.isTableNameValid(name, excludeTable);
    }

    /**
     * Returns the index of the given table in the repository.
     */
    public int getTableIndex(Table table) {
        return tableRepository.getTableIndex(table);
    }

    /**
     * Returns the name of the table at the given index.
     */
    public String getTableName(int tableIndex) {
        return tableRepository.getTableName(tableIndex);
    }

    /**
     * Returns the number of tables in the repository (same as tablesCount()).
     */
    public int getTablesCount() {
        return tableRepository.getTablesCount();
    }

    // ===== Command Handlers =====

    /**
     * Handles the creation of a new table by executing an AddTableCommand.
     * The new table and its controller are both added to the appropriate lists.
     */
    public void handleCreateNewTableRequest() {
        AddTableCommand cmd = new AddTableCommand(tableRepository, controllers);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Handles deletion of the table at the specified index by executing a DeleteTableCommand.
     */
    public void handleDeleteTableRequest(int tableIndex) {
        Table table = tableRepository.getDeepCopyTable(tableIndex);
        DeleteTableCommand cmd = new DeleteTableCommand(tableRepository, table, tableIndex);
        UndoManager.getInstance().execute(cmd);
    }

    /**
     * Handles renaming of the table at the specified index by executing a RenameTableCommand.
     */
    public void rename(int index, String newName) {
        RenameTableCommand cmd = new RenameTableCommand(tableRepository, index, newName);
        UndoManager.getInstance().execute(cmd);
    }
}