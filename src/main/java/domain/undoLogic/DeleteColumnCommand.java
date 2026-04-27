package domain.undoLogic;

import domain.Column;
import domain.Table;

/**
 * A command that removes a column from a table and allows undoing the action.
 * The column can be restored to its original index upon undo.
 */
public class DeleteColumnCommand implements Command {
    
    private final Table table;
    private final Column column;
    private final int colIndex;

    /**
     * Constructs a command to delete a column from the given table.
     *
     * @param table    the table from which to remove the column (must not be null)
     * @param colIndex the index of the column to remove
     * @throws NullPointerException if table is null
     */
    public DeleteColumnCommand(Table table, int colIndex) {
        if (table == null) throw new NullPointerException("Table must not be null");
        this.table = table;
        this.column = table.getColumn(colIndex);
        this.colIndex = colIndex;
    }

    /**
     * Executes the column deletion.
     */
    @Override
    public void execute() {
        table.removeColumn(colIndex);
    }

    /**
     * Undoes the column deletion by re-inserting the column at its original index.
     */
    @Override
    public void undo() {
        table.addColumn(colIndex, column);
    }
}