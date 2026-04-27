package domain.undoLogic;

import domain.Row;
import domain.Table;

/**
 * A command that deletes a row from a table and allows the operation to be undone.
 * The row can be restored at its original index upon undo.
 */
public class DeleteRowCommand implements Command {

    private final Table table;
    private final Row row;
    private final int index;

    /**
     * Constructs a command to delete a row at the specified index from the given table.
     *
     * @param table  the table from which to remove the row (must not be null)
     * @param index  the index of the row to remove
     * @throws NullPointerException if table is null
     */
    public DeleteRowCommand(Table table, int index) {
        if (table == null) throw new NullPointerException("Table must not be null");
        this.table = table;
        this.row = table.getRow(index);
        this.index = index;
    }

    /**
     * Executes the row deletion.
     */
    @Override
    public void execute() {
        table.deleteRow(index);
    }

    /**
     * Undoes the row deletion by re-inserting the row at its original index.
     */
    @Override
    public void undo() {
        table.addRow(index, row);
    }
}