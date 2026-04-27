package domain.undoLogic;

import java.util.Objects;

import domain.Table;

/**
 * Command that changes the default value of a column in a table.
 * Stores the old and new default values to support undo/redo functionality.
 */
public class ColumnDefaultValueCommand implements Command {

    private final Table table;
    private final int index;
    private final String oldValue;
    private final String newValue;

    /**
     * Constructs a command to change the default value of a column.
     *
     * @param table     the table containing the column; must not be null
     * @param index     the index of the column; must be non-negative
     * @param newValue  the new default value; must not be null
     * @throws NullPointerException     if table or newValue is null
     * @throws IllegalArgumentException if index is negative
     */
    public ColumnDefaultValueCommand(Table table, int index, String newValue) {
        this.table = Objects.requireNonNull(table, "table must not be null");
        if (index < 0) {
            throw new IllegalArgumentException("index must be non-negative");
        }
        this.index = index;
        this.oldValue = table.getDefaultValue(index);
        this.newValue = Objects.requireNonNull(newValue, "newValue must not be null");
    }

    /**
     * Executes the command by setting the new default value.
     */
    @Override
    public void execute() {
        table.setDefaultValue(index, newValue);
    }

    /**
     * Undoes the command by restoring the previous default value.
     */
    @Override
    public void undo() {
        table.setDefaultValue(index, oldValue);
    }
}