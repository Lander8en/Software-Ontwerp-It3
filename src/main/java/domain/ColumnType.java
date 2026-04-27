package domain;

/**
 * Represents the possible data types a column can have.
 * Supports cycling to the next type in a fixed order.
 */
public enum ColumnType {
    STRING,
    EMAIL,
    BOOLEAN,
    INTEGER;

    /**
     * Returns the next ColumnType in a fixed cyclic order.
     * STRING → EMAIL → BOOLEAN → INTEGER → STRING → ...
     *
     * @return the next ColumnType in the cycle
     */
    public ColumnType next() {
        return switch (this) {
            case STRING -> EMAIL;
            case EMAIL -> BOOLEAN;
            case BOOLEAN -> INTEGER;
            case INTEGER -> STRING;
        };
    }

    /**
     * Returns the default column type.
     *
     * @return the default ColumnType (STRING)
     */
    public static ColumnType getDefault() {
        return STRING;
    }
}