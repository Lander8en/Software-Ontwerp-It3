package domain.undoLogic;

import domain.ColumnType;
import domain.Table;

/**
 * A command that sets the type of a column in a table and supports undoing the type change.
 */
public class SetColumnTypeCommand implements Command {
    
    private final Table table;
    private final int colIndex;
    private final ColumnType oldType;
    private final ColumnType newType;

    /**
     * Constructs a SetColumnTypeCommand that changes the type of a column.
     *
     * @param table    the table containing the column (must not be null)
     * @param colIndex the index of the column to modify
     * @param newType  the new type to set for the column (must not be null)
     * @throws NullPointerException if table or newType is null
     */
    public SetColumnTypeCommand(Table table, int colIndex, ColumnType newType) {
        if (table == null) throw new NullPointerException("Table must not be null");
        if (newType == null) throw new NullPointerException("New column type must not be null");
        
        this.table = table;
        this.colIndex = colIndex;
        this.oldType = table.getType(colIndex);
        this.newType = newType;
    }

    /**
     * Executes the type change on the specified column.
     */
    @Override
    public void execute() {
        table.setColumnType(colIndex, newType);
    }

    /**
     * Undoes the type change by restoring the original column type.
     */
    @Override
    public void undo() {
        table.setColumnType(colIndex, oldType);
    }
}