package domain;

/**
 * Represents a column in a table, including its name, type,
 * whether blank values are allowed, and a default value.
 */
public class Column {

    private String name;
    private ColumnType type;
    private boolean blanksAllowed = true;
    private String defaultValue = "";

    // ==== Constructors ====

    /**
     * Constructs a new Column with the given name.
     *
     * @param name the name of the column, must not be null
     * @throws IllegalArgumentException if the name is null
     */
    public Column(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Column name cannot be null");
        }
        this.name = name;
        this.type = ColumnType.getDefault();
    }

    // ==== Public getters and accessors ====

    /**
     * Returns the name of this column.
     *
     * @return the column name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the type of this column.
     *
     * @return the column type
     */
    public ColumnType getType() {
        return type;
    }

    /**
     * Returns whether blank values are allowed in this column.
     *
     * @return true if blanks are allowed; false otherwise
     */
    public boolean isBlanksAllowed() {
        return blanksAllowed;
    }

    /**
     * Returns the default value for this column.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns a new column with the same name but without copying other settings.
     *
     * @return a shallow copy of the column with default attributes
     */
    public Column emptyCopy() {
        return new Column(getName());
    }

    // ==== Public modifiers ====

    /**
     * Sets the name of this column.
     *
     * @param newName the new name to assign, must not be null
     * @throws IllegalArgumentException if newName is null
     */
    public void setName(String newName) {
        if (newName == null) {
            throw new IllegalArgumentException("Column name cannot be null");
        }
        this.name = newName;
    }

    /**
     * Sets the type of this column.
     *
     * @param type the new type to assign, must not be null
     * @throws IllegalArgumentException if type is null
     */
    public void setType(ColumnType type) {
        if (type == null) {
            throw new IllegalArgumentException("Column type cannot be null");
        }
        this.type = type;
    }

    /**
     * Sets whether blank values are allowed in this column.
     *
     * @param allowBlanks true to allow blanks; false to disallow
     */
    public void setBlanksAllowed(boolean allowBlanks) {
        this.blanksAllowed = allowBlanks;
    }

    /**
     * Sets the default value for this column.
     *
     * @param defaultValue the default value to assign, must not be null
     * @throws IllegalArgumentException if defaultValue is null
     */
    public void setDefaultValue(String defaultValue) {
        if (defaultValue == null) {
            throw new IllegalArgumentException("Default value cannot be null");
        }
        this.defaultValue = defaultValue;
    }

    // ==== Equality and hashing ====

    /**
     * Checks equality based on the column's name.
     *
     * @param obj the object to compare with
     * @return true if the names are equal; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Column other = (Column) obj;
        return name.equals(other.name);
    }

    /**
     * Returns the hash code based on the column name.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}