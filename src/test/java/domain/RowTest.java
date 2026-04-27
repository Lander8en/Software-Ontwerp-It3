package domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class RowTest {

    @Test
    public void testConstructorInitializesWithDefaultValues() {
        Row row = new Row(3, Arrays.asList("A", "B", "C"));

        assertEquals("A", row.getValue(0));
        assertEquals("B", row.getValue(1));
        assertEquals("C", row.getValue(2));
    }

    @Test
    public void testSetValueChangesTheValueAtGivenIndex() {
        Row row = new Row(2, Arrays.asList("X", "Y"));
        row.setValue(0, "Z");

        assertEquals("Z", row.getValue(0));
        assertEquals("Y", row.getValue(1)); // Ensure other values are unchanged
    }

    @Test
    public void testGetValueThrowsExceptionForInvalidIndex() {
        Row row = new Row(2, Arrays.asList("1", "2"));

        assertThrows(IndexOutOfBoundsException.class, () -> row.getValue(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> row.getValue(2));
    }

    @Test
    public void testSetValueThrowsExceptionForInvalidIndex() {
        Row row = new Row(2, Arrays.asList("A", "B"));

        assertThrows(IndexOutOfBoundsException.class, () -> row.setValue(2, "C"));
    }

    @Test
    public void testValuesPreserveOrder() {
        Row row = new Row(3, Arrays.asList("first", "second", "third"));

        assertEquals("first", row.getValue(0));
        assertEquals("second", row.getValue(1));
        assertEquals("third", row.getValue(2));
    }

    @Test
    void constructorThrowsIfDefaultValuesIsNull() {
        assertThrows(NullPointerException.class, () -> new Row(1, null));
    }

    @Test
    void constructorThrowsIfDefaultValueIsNullElement() {
        assertThrows(NullPointerException.class, () -> new Row(2, Arrays.asList("A", null)));
    }

    @Test
    void constructorThrowsIfDefaultValuesTooShort() {
        assertThrows(IllegalArgumentException.class, () -> new Row(3, Arrays.asList("A", "B")));
    }

    @Test
    void addValueAppendsToEnd() {
        Row row = new Row(1, Arrays.asList("X"));
        row.addValue("Y");
        assertEquals("Y", row.getValue(1));
    }

    @Test
    void addValueThrowsIfNull() {
        Row row = new Row(1, Arrays.asList("X"));
        assertThrows(NullPointerException.class, () -> row.addValue(null));
    }

    @Test
    void setValueThrowsIllegalArgumentExceptionIfValueIsNull() {
        Row row = new Row(2, Arrays.asList("A", "B"));
        Exception ex = assertThrows(NullPointerException.class, () -> row.setValue(1, null));
        assertEquals("newValue must not be null", ex.getMessage()); // Confirms the specific branch
    }

}
