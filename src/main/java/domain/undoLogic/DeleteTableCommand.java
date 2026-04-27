package domain.undoLogic;

import domain.Table;
import domain.TableRepository;

/**
 * A command that deletes a table from the repository and allows undoing the operation.
 * When undone, the table is reinserted at its original index.
 */
public class DeleteTableCommand implements Command {

    private final TableRepository repo;
    private final Table table;
    private final int tableIndex;

    /**
     * Constructs a command to delete a table from a repository.
     *
     * @param repo        the repository to modify (must not be null)
     * @param table       the table to delete (must not be null)
     * @param tableIndex  the index at which the table was originally located
     * @throws NullPointerException if repo or table is null
     */
    public DeleteTableCommand(TableRepository repo, Table table, int tableIndex) {
        if (repo == null) throw new NullPointerException("TableRepository must not be null");
        if (table == null) throw new NullPointerException("Table must not be null");
        this.repo = repo;
        this.table = table;
        this.tableIndex = tableIndex;
    }

    /**
     * Executes the deletion of the table from the repository.
     */
    @Override
    public void execute() {
        repo.remove(table);
    }

    /**
     * Undoes the deletion by adding the table back to its original position.
     */
    @Override
    public void undo() {
        repo.addTable(tableIndex, table);
    }
}