package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single row in a table, holding a list of string values.
 * Each index in the list corresponds to a column in the table.
 */
public class Row {

    private final List<String> values;

    // ==== Constructor ====

    /**
     * Constructs a new Row with initial values from the given list.
     *
     * @param columnCount   the number of columns this row should have
     * @param defaultValues the default values to use for each column;
     *                      must not be null and must contain at least columnCount elements
     * @throws IllegalArgumentException if defaultValues has fewer than columnCount elements
     * @throws NullPointerException     if defaultValues or any of its elements are null
     */
    public Row(int columnCount, List<String> defaultValues) {
        Objects.requireNonNull(defaultValues, "defaultValues must not be null");
        if (defaultValues.size() < columnCount) {
            throw new IllegalArgumentException("defaultValues must contain at least " + columnCount + " elements");
        }

        values = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            String value = Objects.requireNonNull(defaultValues.get(i), "default value at index " + i + " is null");
            values.add(value);
        }
    }

    // ==== Copy ====

    /**
     * Returns a deep copy of this row.
     *
     * @return a new Row instance with copied values
     */
    public Row getRow() {
        return new Row(values.size(), new ArrayList<>(values));
    }

    // ==== Getters ====

    /**
     * Returns the value at the specified column index.
     *
     * @param index the column index
     * @return the value at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public String getValue(int index) {
        return values.get(index);
    }

    // ==== Modifiers ====

    /**
     * Sets the value at the specified column index.
     *
     * @param index    the column index
     * @param newValue the new value to set; must not be null
     * @throws NullPointerException      if newValue is null
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void setValue(int index, String newValue) {
        values.set(index, Objects.requireNonNull(newValue, "newValue must not be null"));
    }

    /**
     * Adds a new value to the end of the row.
     *
     * @param value the value to add; must not be null
     * @throws NullPointerException if value is null
     */
    public void addValue(String value) {
        values.add(Objects.requireNonNull(value, "value must not be null"));
    }
}