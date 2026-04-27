package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColumnTypeValidatorTest {

    private Table setupTableWithColumn(Column column, String... values) {
        Table table = new Table("TestTable");
        table.addColumn(0, column);

        for (String value : values) {
            Row row = new Row(1, List.of(value));
            table.addRow(table.getRowsCount(), row);
        }

        return table;
    }

    @Test
    void validStringColumn_allValuesAccepted() {
        Column column = new Column("Name");
        column.setType(ColumnType.STRING);
        column.setDefaultValue("John");

        Table table = setupTableWithColumn(column, "Doe", "Smith");

        assertTrue(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void validEmailColumn_withProperEmails() {
        Column column = new Column("Email");
        column.setType(ColumnType.EMAIL);
        column.setDefaultValue("test@example.com");

        Table table = setupTableWithColumn(column, "user@domain.com");

        assertTrue(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void invalidEmailColumn_withMissingAtSymbol() {
        Column column = new Column("Email");
        column.setType(ColumnType.EMAIL);
        column.setDefaultValue("test@example.com");

        Table table = setupTableWithColumn(column, "invalidEmail.com");

        assertFalse(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void validBooleanColumn_withTrueFalse() {
        Column column = new Column("Active");
        column.setType(ColumnType.BOOLEAN);
        column.setDefaultValue("true");

        Table table = setupTableWithColumn(column, "false", "true");

        assertTrue(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void invalidBooleanColumn_withRandomValue() {
        Column column = new Column("Active");
        column.setType(ColumnType.BOOLEAN);
        column.setDefaultValue("true");

        Table table = setupTableWithColumn(column, "yes");

        assertFalse(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void validIntegerColumn_withValidNumbers() {
        Column column = new Column("Age");
        column.setType(ColumnType.INTEGER);
        column.setDefaultValue("30");

        Table table = setupTableWithColumn(column, "-42", "0", "100");

        assertTrue(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void invalidIntegerColumn_withLetters() {
        Column column = new Column("Age");
        column.setType(ColumnType.INTEGER);
        column.setDefaultValue("30");

        Table table = setupTableWithColumn(column, "abc");

        assertFalse(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void blankValueAllowed_returnsTrue() {
        Column column = new Column("Optional");
        column.setType(ColumnType.STRING);
        column.setDefaultValue("");
        column.setBlanksAllowed(true);

        Table table = setupTableWithColumn(column, "   ");

        assertTrue(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void blankValueDisallowed_returnsTrueForStringType() {
        Column column = new Column("Required");
        column.setType(ColumnType.STRING);
        column.setDefaultValue("");
        column.setBlanksAllowed(false); // blanks disallowed

        Table table = new Table("MyTable");
        table.addColumn(0, column);
        table.addRow(0, new Row(1, List.of(" "))); // blank string (space)

        // Will return true because STRING type bypasses blank check
        assertTrue(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void columnNotInTable_returnsFalse() {
        Column column = new Column("Ghost");
        Table table = new Table("Haunted");

        assertFalse(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

    @Test
    void nullColumn_throwsException() {
        Table table = new Table("Test");

        assertThrows(NullPointerException.class, () -> ColumnTypeValidator.isColumnTypeValid(null, table));
    }

    @Test
    void nullTable_throwsException() {
        Column column = new Column("X");

        assertThrows(NullPointerException.class, () -> ColumnTypeValidator.isColumnTypeValid(column, null));
    }

    @Test
    void nullValue_returnsFalse() {
        assertFalse(ColumnTypeValidator.isValidValue(ColumnType.STRING, null, true));
        assertFalse(ColumnTypeValidator.isValidValue(ColumnType.INTEGER, null, false));
    }

    @Test
    void invalidDefaultEmailValue_returnsFalse() {
        Column column = new Column("Email");
        column.setType(ColumnType.EMAIL);
        column.setDefaultValue("invalid-email"); // no "@"
        Table table = setupTableWithColumn(column, "valid@domain.com");

        assertFalse(ColumnTypeValidator.isColumnTypeValid(column, table));
    }

}
