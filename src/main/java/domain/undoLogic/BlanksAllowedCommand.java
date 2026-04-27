package domain.undoLogic;

import java.util.Objects;

import domain.Table;

/**
 * Command to toggle the "blanks allowed" setting for a specific column in a table.
 * Toggling is symmetric, so executing and undoing perform the same operation.
 */
public class BlanksAllowedCommand implements Command {

    private final Table table;
    private final int index;

    /**
     * Constructs a BlanksAllowedCommand for a specific column in a table.
     *
     * @param table the table containing the column; must not be null
     * @param index the index of the column to toggle
     * @throws NullPointerException     if table is null
     * @throws IllegalArgumentException if index is negative
     */
    public BlanksAllowedCommand(Table table, int index) {
        this.table = Objects.requireNonNull(table, "table must not be null");
        if (index < 0) {
            throw new IllegalArgumentException("index must be non-negative");
        }
        this.index = index;
    }

    /**
     * Executes the command by toggling whether blanks are allowed in the column.
     */
    @Override
    public void execute() {
        table.toggleBlanksAllowed(index);
    }

    /**
     * Undoes the command by toggling the same setting again.
     */
    @Override
    public void undo() {
        table.toggleBlanksAllowed(index);
    }
}