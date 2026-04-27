package domain.undoLogic;

import domain.Column;
import domain.Table;
import java.util.Objects;

/**
 * Command representing the addition of a new column to a table.
 * This operation can be undone by removing the column again.
 */
public class AddColumnCommand implements Command {

    private final Table table;
    private final Column column;
    private final int colIndex;

    /**
     * Constructs a new AddColumnCommand for the specified table.
     * A new column is immediately created and stored for future execution or undo.
     *
     * @param table the table to which a column will be added; must not be null
     * @throws NullPointerException if table is null
     */
    public AddColumnCommand(Table table) {
        this.table = Objects.requireNonNull(table, "table must not be null");
        this.column = table.createNewColumn();
        this.colIndex = table.getColumnsCount();
    }

    /**
     * Executes the command by adding the stored column at the recorded index.
     */
    @Override
    public void execute() {
        table.addColumn(colIndex, column);
    }

    /**
     * Undoes the column addition by removing the column at the recorded index.
     */
    @Override
    public void undo() {
        table.removeColumn(colIndex);
    }
}