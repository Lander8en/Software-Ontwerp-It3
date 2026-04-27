package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnTest {

    @Test
    public void testConstructorWithName() {
        Column column = new Column("TestColumn");
        assertEquals("TestColumn", column.getName());
        assertEquals(ColumnType.STRING, column.getType());
        assertTrue(column.isBlanksAllowed());
        assertEquals("", column.getDefaultValue());
    }

    @Test
    public void testConstructorWithNullNameThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Column(null);
        });
        assertEquals("Column name cannot be null", exception.getMessage());
    }

    @Test
    public void testSetName() {
        Column column = new Column("OldName");
        column.setName("NewName");
        assertEquals("NewName", column.getName());
    }

    @Test
    public void testSetNameToNullThrowsException() {
        Column column = new Column("TestColumn");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            column.setName(null);
        });
        assertEquals("Column name cannot be null", exception.getMessage());
    }

    @Test
    public void testSetType() {
        Column column = new Column("TestColumn");
        column.setType(ColumnType.INTEGER);
        assertEquals(ColumnType.INTEGER, column.getType());
    }

    @Test
    public void testSetTypeToNullThrowsException() {
        Column column = new Column("TestColumn");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            column.setType(null);
        });
        assertEquals("Column type cannot be null", exception.getMessage());
    }

    @Test
    public void testSetAndIsBlanksAllowed() {
        Column column = new Column("TestColumn");
        column.setBlanksAllowed(false);
        assertFalse(column.isBlanksAllowed());
    }

    @Test
    public void testSetAndGetDefaultValue() {
        Column column = new Column("TestColumn");
        column.setDefaultValue("Default");
        assertEquals("Default", column.getDefaultValue());
    }

    @Test
    public void testSetDefaultValueToNullThrowsException() {
        Column column = new Column("TestColumn");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            column.setDefaultValue(null);
        });
        assertEquals("Default value cannot be null", exception.getMessage());
    }

    @Test
    public void testChangeTypeMultipleTimes() {
        Column column = new Column("MultiType");
        column.setType(ColumnType.BOOLEAN);
        assertEquals(ColumnType.BOOLEAN, column.getType());

        column.setType(ColumnType.INTEGER);
        assertEquals(ColumnType.INTEGER, column.getType());

        column.setType(ColumnType.STRING);
        assertEquals(ColumnType.STRING, column.getType());
    }

    @Test
    public void testBlanksAllowedDefaultIsTrue() {
        Column column = new Column("BlanksCheck");
        assertTrue(column.isBlanksAllowed());
    }

    @Test
    public void testDefaultValueIsInstanceSpecific() {
        Column col1 = new Column("Col1");
        Column col2 = new Column("Col2");

        col1.setDefaultValue("A");
        col2.setDefaultValue("B");

        assertEquals("A", col1.getDefaultValue());
        assertEquals("B", col2.getDefaultValue());
    }

    @Test
    public void testSetSameNameTwiceIsSafe() {
        Column column = new Column("SameName");
        column.setName("SameName");
        assertEquals("SameName", column.getName());
    }

    @Test
    public void testDefaultTypeIsString() {
        Column column = new Column("TestDefaultType");
        assertEquals(ColumnType.STRING, column.getType());
    }

    @Test
    public void testEquals_sameName_shouldBeEqual() {
        Column col1 = new Column("Same");
        Column col2 = new Column("Same");
        assertEquals(col1, col2);
    }

    @Test
    public void testEquals_differentName_shouldNotBeEqual() {
        Column col1 = new Column("Col1");
        Column col2 = new Column("Col2");
        assertNotEquals(col1, col2);
    }

    @Test
    public void testEquals_nullOrDifferentClass_shouldReturnFalse() {
        Column col = new Column("Col");
        assertNotEquals(col, null);
        assertNotEquals(col, "NotAColumn");
    }

    @Test
    public void testHashCode_sameName_shouldMatch() {
        Column col1 = new Column("HashCol");
        Column col2 = new Column("HashCol");
        assertEquals(col1.hashCode(), col2.hashCode());
    }

    @Test
    public void testEmptyCopy_hasSameNameButDefaultAttributes() {
        Column original = new Column("CopyMe");
        original.setBlanksAllowed(false);
        original.setDefaultValue("SomeValue");
        original.setType(ColumnType.INTEGER);

        Column copy = original.emptyCopy();

        assertEquals("CopyMe", copy.getName());
        assertEquals(ColumnType.STRING, copy.getType()); // default type
        assertTrue(copy.isBlanksAllowed()); // default blanksAllowed
        assertEquals("", copy.getDefaultValue()); // default defaultValue
    }

}
