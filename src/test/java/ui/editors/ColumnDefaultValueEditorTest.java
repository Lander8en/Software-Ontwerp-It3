package ui.editors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static ui.UIConstants.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

import domain.Column;
import domain.ColumnType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColumnDefaultValueEditorTest {

    private ColumnDefaultValueAccess mockValueAccess;
    private Graphics mockGraphics;
    private ColumnDefaultValueEditor editor;
    private Column mockColumn;

    @BeforeEach
    void setUp() {
        mockValueAccess = mock(ColumnDefaultValueAccess.class);
        mockGraphics = mock(Graphics.class);
        editor = new ColumnDefaultValueEditor(mockValueAccess);
        mockColumn = mock(Column.class);
    }

    @Test
    void startEditing_SetsUpEditingState() {
        when(mockValueAccess.getValue(0)).thenReturn("initial");

        editor.startEditing(0);

        assertTrue(editor.isEditing());
        assertEquals(0, editor.getEditingTargetIndex());
    }

    @Test
    void handleKeyEvent_EnterCommitsValidEdit() {
        when(mockValueAccess.getValue(0)).thenReturn("");
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);

        editor.startEditing(0);
        typeCharacters("test");

        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ENTER, '\0');

        verify(mockValueAccess).setValue(0, "test");
        assertFalse(editor.isEditing());
    }

    @Test
    void handleKeyEvent_EscapeCancelsEditing() {
        editor.startEditing(0);
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ESCAPE, '\0');

        assertFalse(editor.isEditing());
        assertEquals(-1, editor.getEditingTargetIndex());
    }

    @Test
    void handleKeyEvent_BackspaceRemovesCharacters() {
        when(mockValueAccess.getValue(0)).thenReturn("test");
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);
        editor.startEditing(0);

        drawAndVerifyString("test_");

        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0');
        drawAndVerifyString("tes_");
    }

    @Test
    void drawValue_DrawsEditingState() {
        when(mockValueAccess.getValue(0)).thenReturn("");
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);

        editor.startEditing(0);
        typeCharacters("edit");

        drawAndVerifyString("edit_");
    }

    @Test
    void drawValue_InvalidHighlighting() {
        when(mockValueAccess.getValue(0)).thenReturn("");
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.EMAIL);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);

        editor.startEditing(0);
        typeCharacters("invalid");

        editor.drawValue(mockGraphics, 0, mockColumn, 10, 20, DEFAULT_VALUE_WIDTH);

        verify(mockGraphics).setColor(Color.RED);
        verify(mockGraphics).drawRect(9, 20, DEFAULT_VALUE_WIDTH, ROW_HEIGHT);
    }

    @Test
    void isValid_BooleanAlwaysTrue() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockValueAccess.getValue(0)).thenReturn(""); // <-- Fix: prevent currentInput = null
        when(mockColumn.getType()).thenReturn(ColumnType.BOOLEAN);

        editor.startEditing(0);
        assertTrue(editor.isValid());
    }

    @Test
    void isValid_EmptyStringAllowedIfBlanksAllowed() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);
        when(mockValueAccess.getValue(0)).thenReturn("");

        editor.startEditing(0);
        assertTrue(editor.isValid());
    }

    @Test
    void commitEdit_DoesNothingIfInvalid() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.EMAIL);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("invalid");

        editor.startEditing(0);
        typeCharacters("invalid");

        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ENTER, '\0');
        verify(mockValueAccess, never()).setValue(anyInt(), any());
    }

    @Test
    void typeBlocking_BlockAndClear() {
        editor.setTypeBlocked(2);
        assertTrue(editor.isTypeBlocked());
        assertTrue(editor.isTypeBlocked(2));
        assertFalse(editor.isTypeBlocked(3));
        assertEquals(2, editor.getBlockedColumnIndex());

        editor.clearTypeBlock();
        assertFalse(editor.isTypeBlocked());
        assertEquals(-1, editor.getBlockedColumnIndex());
    }

    @Test
    void stopEditingIfChanged_StopsOnInvalidEdit() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.EMAIL);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("invalid");

        editor.startEditing(0);
        typeCharacters("invalid");

        editor.stopEditingIfChanged(0);
        assertFalse(editor.isEditing());
    }

    @Test
    void stopEditingIfChanged_IgnoresOtherIndex() {
        editor.startEditing(0);
        editor.stopEditingIfChanged(1); // no effect
        assertTrue(editor.isEditing());
    }

    private void typeCharacters(String text) {
        for (char c : text.toCharArray()) {
            editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, c);
        }
    }

    private void drawAndVerifyString(String expected) {
        reset(mockGraphics);
        editor.drawValue(mockGraphics, 0, mockColumn, 10, 20, DEFAULT_VALUE_WIDTH);
        verify(mockGraphics).drawString(expected, 12, 35);
    }

    @Test
    void continueEditing_ChangesTargetIndex() {
        when(mockValueAccess.getValue(1)).thenReturn("abc");
        editor.startEditing(0);
        editor.continueEditing(1);

        assertEquals(1, editor.getEditingTargetIndex());
        assertTrue(editor.isEditing());
    }

    @Test
    void stopEditing_ResetsAllState() {
        when(mockValueAccess.getValue(0)).thenReturn("xyz");
        editor.startEditing(0);
        editor.stopEditing();

        assertFalse(editor.isEditing());
        assertEquals(-1, editor.getEditingTargetIndex());
    }

    @Test
    void isValid_ColumnCheck_StringAndInteger() {
        Column stringCol = mock(Column.class);
        when(stringCol.getType()).thenReturn(ColumnType.STRING);
        when(stringCol.getDefaultValue()).thenReturn("hello");
        when(stringCol.isBlanksAllowed()).thenReturn(true);

        Column intCol = mock(Column.class);
        when(intCol.getType()).thenReturn(ColumnType.INTEGER);
        when(intCol.getDefaultValue()).thenReturn("123");
        when(intCol.isBlanksAllowed()).thenReturn(false);

        assertTrue(editor.isValid(stringCol));
        assertTrue(editor.isValid(intCol));
    }

    @Test
    void isValid_ColumnCheck_IntegerWithLeadingZero() {
        Column col = mock(Column.class);
        when(col.getType()).thenReturn(ColumnType.INTEGER);
        when(col.getDefaultValue()).thenReturn("0123");
        when(col.isBlanksAllowed()).thenReturn(false);

        assertFalse(editor.isValid(col));
    }

    @Test
    void isValid_ColumnCheck_BooleanCases() {
        Column trueCol = mock(Column.class);
        when(trueCol.getType()).thenReturn(ColumnType.BOOLEAN);
        when(trueCol.getDefaultValue()).thenReturn("true");

        Column falseCol = mock(Column.class);
        when(falseCol.getType()).thenReturn(ColumnType.BOOLEAN);
        when(falseCol.getDefaultValue()).thenReturn("false");

        Column invalid = mock(Column.class);
        when(invalid.getType()).thenReturn(ColumnType.BOOLEAN);
        when(invalid.getDefaultValue()).thenReturn("yes");

        assertTrue(editor.isValid(trueCol));
        assertTrue(editor.isValid(falseCol));
        assertFalse(editor.isValid(invalid));
    }

    @Test
    void handleKeyEvent_NonEditingModeIgnored() {
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0'); // Should not throw
        assertFalse(editor.isEditing());
    }

    @Test
    void handleKeyEvent_InvalidCharactersIgnored() {
        when(mockValueAccess.getValue(0)).thenReturn("");

        // Fix: Provide at least one column so isValid() does not throw
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);

        editor.startEditing(0);

        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, '#');
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, ' ');

        drawAndVerifyString("_"); // Only cursor shown, no characters added
    }

    @Test
    void commitEdit_IgnoredIfIndexUnset() {
        editor.commitEdit(); // editingTargetIndex == -1
        verify(mockValueAccess, never()).setValue(anyInt(), any());
    }

    @Test
    void isValid_String_NotBlank_DisallowedBlanks() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("text");

        editor.startEditing(0);
        assertTrue(editor.isValid());
    }

    @Test
    void isValid_String_Blank_DisallowedBlanks() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("");

        editor.startEditing(0);
        assertFalse(editor.isValid());
    }

    @Test
    void isValid_Email_Valid() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.EMAIL);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("a@b");

        editor.startEditing(0);
        assertTrue(editor.isValid());
    }

    @Test
    void isValid_Email_Invalid_MultipleAtSymbols() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.EMAIL);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("a@b@c");

        editor.startEditing(0);
        assertFalse(editor.isValid());
    }

    @Test
    void isValid_Email_Blank_Allowed() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.EMAIL);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);
        when(mockValueAccess.getValue(0)).thenReturn("");

        editor.startEditing(0);
        assertTrue(editor.isValid());
    }

    @Test
    void isValid_Integer_Valid() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.INTEGER);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("123");

        editor.startEditing(0);
        assertTrue(editor.isValid());
    }

    @Test
    void isValid_Integer_InvalidAlpha() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.INTEGER);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("12a3");

        editor.startEditing(0);
        assertFalse(editor.isValid());
    }

    @Test
    void isValid_Integer_Blank_Disallowed() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.INTEGER);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("");

        editor.startEditing(0);
        assertFalse(editor.isValid());
    }

    @Test
    void isValid_Integer_Blank_Allowed() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.INTEGER);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);
        when(mockValueAccess.getValue(0)).thenReturn("");

        editor.startEditing(0);
        assertTrue(editor.isValid());
    }

    @Test
    void isValid_Integer_LeadingZeros() {
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
        when(mockColumn.getType()).thenReturn(ColumnType.INTEGER);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockValueAccess.getValue(0)).thenReturn("007");

        editor.startEditing(0);
        assertFalse(editor.isValid()); // Fails strict integer formatting check
    }

    @Test
    void isValid_String_AnyNonBlankAccepted() {
        setupColumnDefault(ColumnType.STRING, false, "abc");
        assertTrue(editor.isValid(mockColumn));
    }

    @Test
    void isValid_String_Blank_Disallowed() {
        setupColumnDefault(ColumnType.STRING, false, "");
        assertFalse(editor.isValid(mockColumn));
    }

    @Test
    void isValid_String_Blank_Allowed() {
        setupColumnDefault(ColumnType.STRING, true, "");
        assertTrue(editor.isValid(mockColumn));
    }

    @Test
    void isValid_Boolean_True() {
        setupColumnDefault(ColumnType.BOOLEAN, false, "true");
        assertTrue(editor.isValid(mockColumn));
    }

    @Test
    void isValid_Boolean_False() {
        setupColumnDefault(ColumnType.BOOLEAN, false, "false");
        assertTrue(editor.isValid(mockColumn));
    }

    @Test
    void isValid_Boolean_InvalidText() {
        setupColumnDefault(ColumnType.BOOLEAN, false, "yes");
        assertFalse(editor.isValid(mockColumn));
    }

    @Test
    void isValid_Boolean_Blank_Disallowed() {
        setupColumnDefault(ColumnType.BOOLEAN, false, "");
        assertFalse(editor.isValid(mockColumn));
    }

    @Test
    void isValid_Boolean_Blank_Allowed() {
        setupColumnDefault(ColumnType.BOOLEAN, true, "");
        assertTrue(editor.isValid(mockColumn));
    }

    @Test
    void isValid_Email_ValidSingleAt() {
        setupColumnDefault(ColumnType.EMAIL, false, "x@y");
        assertTrue(editor.isValid(mockColumn));
    }

    @Test
    void isValid_Email_InvalidMultipleAt() {
        setupColumnDefault(ColumnType.EMAIL, false, "a@b@c");
        assertFalse(editor.isValid(mockColumn));
    }

    @Test
    void isValid_Email_Blank_Disallowed() {
        setupColumnDefault(ColumnType.EMAIL, false, "");
        assertFalse(editor.isValid(mockColumn));
    }

    private void setupColumnDefault(ColumnType type, boolean blanksAllowed, String defaultValue) {
        when(mockColumn.getType()).thenReturn(type);
        when(mockColumn.isBlanksAllowed()).thenReturn(blanksAllowed);
        when(mockColumn.getDefaultValue()).thenReturn(defaultValue);
        when(mockValueAccess.getAllItems()).thenReturn(List.of(mockColumn));
    }
}
