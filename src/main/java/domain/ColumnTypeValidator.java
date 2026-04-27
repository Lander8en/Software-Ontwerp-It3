package domain;

/**
 * Utility class to validate whether a column's type is compatible with
 * its default value and all values in a table's rows.
 */
public class ColumnTypeValidator {

    /**
     * Validates whether the specified column's type is compatible with:
     * - its default value
     * - all current values in the column across the table's rows
     *
     * @param column the column to validate
     * @param table the table containing the column and rows
     * @return true if the column type is valid for all associated values; false otherwise
     * @throws NullPointerException if either column or table is null
     */
    public static boolean isColumnTypeValid(Column column, Table table) {
        if (column == null || table == null) {
            throw new NullPointerException("Column and Table must not be null");
        }

        ColumnType type = column.getType();
        int colIndex = table.getColumns().indexOf(column);
        if (colIndex == -1) return false;

        // Validate the column's default value
        if (!isValidValue(type, column.getDefaultValue(), column.isBlanksAllowed())) {
            return false;
        }

        // Validate all values in the column across the table
        for (Row row : table.getRows()) {
            String value = row.getValue(colIndex);
            if (!isValidValue(type, value, column.isBlanksAllowed())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if a given value is valid for a specific column type
     * and respects the blanks-allowed policy.
     *
     * @param type the type of the column
     * @param value the value to validate
     * @param allowBlank whether blanks are allowed
     * @return true if the value is valid for the type; false otherwise
     */
    public static boolean isValidValue(ColumnType type, String value, boolean allowBlank) {
        if (value == null) return false;
        if (value.isBlank() && allowBlank) return true;

        return switch (type) {
            case STRING -> true;
            case EMAIL -> value.contains("@");
            case BOOLEAN -> value.equals("true") || value.equals("false");
            case INTEGER -> value.matches("-?\\d+");
        };
    }
}