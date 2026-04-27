package domain.undoLogic;

import java.util.Objects;

import domain.Table;

/**
 * Command to change the value of a specific cell in a table.
 * Stores both the old and new values to support undo and redo functionality.
 */
public class ChangeRowValueCommand implements Command {

    private final Table table;
    private final int rowIndex;
    private final int colIndex;
    private final String oldValue;
    private final String newValue;

    /**
     * Constructs a command to change a cell value in a table.
     *
     * @param table     the table in which the value should be changed; must not be null
     * @param rowIndex  the index of the row containing the cell; must be non-negative
     * @param colIndex  the index of the column containing the cell; must be non-negative
     * @param newValue  the new value to set; must not be null
     * @throws NullPointerException     if table or newValue is null
     * @throws IllegalArgumentException if rowIndex or colIndex is negative
     */
    public ChangeRowValueCommand(Table table, int rowIndex, int colIndex, String newValue) {
        this.table = Objects.requireNonNull(table, "table must not be null");
        if (rowIndex < 0 || colIndex < 0) {
            throw new IllegalArgumentException("rowIndex and colIndex must be non-negative");
        }
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.oldValue = table.getValue(rowIndex, colIndex);
        this.newValue = Objects.requireNonNull(newValue, "newValue must not be null");
    }

    /**
     * Executes the command by applying the new value.
     */
    @Override
    public void execute() {
        table.setRowValue(rowIndex, colIndex, newValue);
    }

    /**
     * Undoes the command by restoring the old value.
     */
    @Override
    public void undo() {
        table.setRowValue(rowIndex, colIndex, oldValue);
    }
}