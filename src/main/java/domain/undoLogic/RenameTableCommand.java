package domain.undoLogic;

import domain.TableRepository;

/**
 * A command that renames a table in the repository and allows undoing the change.
 */
public class RenameTableCommand implements Command {
    
    private final TableRepository repo;
    private final int tableIndex;
    private final String newName;
    private final String oldName;

    /**
     * Constructs a RenameTableCommand for renaming a table in the repository.
     *
     * @param repo        the repository containing the table (must not be null)
     * @param tableIndex  the index of the table to rename
     * @param newName     the new name to assign (must not be null)
     * @throws NullPointerException if repo or newName is null
     */
    public RenameTableCommand(TableRepository repo, int tableIndex, String newName) {
        if (repo == null) throw new NullPointerException("TableRepository must not be null");
        if (newName == null) throw new NullPointerException("New name must not be null");

        this.repo = repo;
        this.tableIndex = tableIndex;
        this.newName = newName;
        this.oldName = repo.getTableName(tableIndex);
    }

    /**
     * Executes the renaming of the table to the new name.
     */
    @Override
    public void execute() {
        repo.updateTableName(tableIndex, newName);
    }

    /**
     * Undoes the table rename by restoring the original name.
     */
    @Override
    public void undo() {
        repo.updateTableName(tableIndex, oldName);
    }
}