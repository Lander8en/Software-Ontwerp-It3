package domain.undoLogic;

import domain.Table;

/**
 * A command that renames a column in a table and supports undoing the change.
 */
public class RenameColumnCommand implements Command {
    
    private final Table table;
    private final int colIndex;
    private final String oldName;
    private final String newName;

    /**
     * Constructs a RenameColumnCommand for a column at the given index in the specified table.
     *
     * @param table    the table containing the column to rename (must not be null)
     * @param colIndex the index of the column to rename
     * @param newName  the new name to assign to the column (must not be null)
     * @throws NullPointerException if table or newName is null
     */
    public RenameColumnCommand(Table table, int colIndex, String newName) {
        if (table == null) throw new NullPointerException("Table must not be null");
        if (newName == null) throw new NullPointerException("New name must not be null");
        this.table = table;
        this.colIndex = colIndex;
        this.oldName = table.getColumnName(colIndex);
        this.newName = newName;
    }

    /**
     * Executes the renaming of the column.
     */
    @Override
    public void execute() {
        table.renameColumn(colIndex, newName);
    }

    /**
     * Undoes the column renaming by restoring the original name.
     */
    @Override
    public void undo() {
        table.renameColumn(colIndex, oldName);
    }
}