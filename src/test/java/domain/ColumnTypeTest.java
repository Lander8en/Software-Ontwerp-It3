package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnTypeTest {

    @Test
    public void testNextFromString() {
        assertEquals(ColumnType.EMAIL, ColumnType.STRING.next());
    }

    @Test
    public void testNextFromEmail() {
        assertEquals(ColumnType.BOOLEAN, ColumnType.EMAIL.next());
    }

    @Test
    public void testNextFromBoolean() {
        assertEquals(ColumnType.INTEGER, ColumnType.BOOLEAN.next());
    }

    @Test
    public void testNextFromInteger() {
        assertEquals(ColumnType.STRING, ColumnType.INTEGER.next());
    }

    @Test
    public void testNextIsCyclic() {
        ColumnType type = ColumnType.STRING;
        type = type.next(); // EMAIL
        type = type.next(); // BOOLEAN
        type = type.next(); // INTEGER
        type = type.next(); // back to STRING
        assertEquals(ColumnType.STRING, type);
    }
}
