package domain.undoLogic;

import domain.Row;
import domain.Table;
import java.util.Objects;

/**
 * Command representing the addition of a new row to a table.
 * This command stores the added row and its index so it can be undone later.
 */
public class AddRowCommand implements Command {

    private final Table table;
    private final Row row;
    private final int index;

    /**
     * Constructs an AddRowCommand that adds a new row to the given table.
     * The new row is created immediately upon construction.
     *
     * @param table the table to which a row will be added; must not be null
     * @throws NullPointerException if table is null
     */
    public AddRowCommand(Table table) {
        this.table = Objects.requireNonNull(table, "table must not be null");
        this.row = table.createNewRow();
        this.index = table.getRowsCount();
    }

    /**
     * Executes the command by adding the stored row at the recorded index.
     */
    @Override
    public void execute() {
        table.addRow(index, row);
    }

    /**
     * Undoes the addition by removing the row at the recorded index.
     */
    @Override
    public void undo() {
        table.deleteRow(index);
    }
}