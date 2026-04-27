package ui.editors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import domain.ColumnType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ui.UIConstants.DEFAULT_VALUE_WIDTH;

public class RowValueEditorTest {

    private RowValueAccess mockAccess;
    private Graphics mockGraphics;
    private RowValueEditor editor;

    @BeforeEach
    void setUp() {
        mockAccess = mock(RowValueAccess.class);
        mockGraphics = mock(Graphics.class);
        editor = new RowValueEditor(mockAccess);
    }

    @Test
    void startEditing_SetsEditingTrue() {
        when(mockAccess.getValue(0, 1)).thenReturn("123");

        editor.startEditing(0, 1);

        assertTrue(editor.isEditing());
    }

    @Test
    void handleKeyEvent_AppendsValidCharacters() {
        when(mockAccess.getValue(0, 0)).thenReturn("");
        when(mockAccess.getType(0)).thenReturn(ColumnType.STRING);
        when(mockAccess.allowsBlanks(0)).thenReturn(true);

        editor.startEditing(0, 0);

        editor.handleKeyEvent(0, 0, 'A');
        editor.handleKeyEvent(0, 0, '1');
        editor.handleKeyEvent(0, 0, ' ');

        editor.commitEdit();

        verify(mockAccess).setValue(0, 0, "A1 ");
    }

    @Test
    void handleKeyEvent_Backspace_RemovesCharacters() {
        when(mockAccess.getValue(0, 0)).thenReturn("ABC");
        when(mockAccess.getType(0)).thenReturn(ColumnType.STRING);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, KeyEvent.VK_BACK_SPACE, '\0');

        editor.commitEdit();
        verify(mockAccess).setValue(0, 0, "AB");
    }

    @Test
    void handleKeyEvent_Escape_StopsEditing() {
        when(mockAccess.getValue(0, 0)).thenReturn("Initial");

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, KeyEvent.VK_ESCAPE, '\0');

        assertFalse(editor.isEditing());
    }

    @Test
    void commitEdit_InvalidEmail_DoesNotCommit() {
        when(mockAccess.getValue(0, 0)).thenReturn("testemail");
        when(mockAccess.getType(0)).thenReturn(ColumnType.EMAIL);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, 0, '@');
        editor.handleKeyEvent(0, 0, '@');
        editor.commitEdit();

        verify(mockAccess, never()).setValue(anyInt(), anyInt(), anyString());
        assertTrue(editor.isEditing());
    }

    @Test
    void commitEdit_ValidInteger_CommitsSuccessfully() {
        when(mockAccess.getValue(0, 0)).thenReturn("");
        when(mockAccess.getType(0)).thenReturn(ColumnType.INTEGER);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, 0, '4');
        editor.handleKeyEvent(0, 0, '2');
        editor.commitEdit();

        verify(mockAccess).setValue(0, 0, "42");
        assertFalse(editor.isEditing());
    }

    @Test
    void draw_WithBooleanValue_RendersCheckmark() {
        when(mockAccess.getValue(0, 0)).thenReturn("true");

        editor.startEditing(0, 0);
        editor.draw(mockGraphics, 0, 0, 10, 20, DEFAULT_VALUE_WIDTH, ColumnType.BOOLEAN, true);

        verify(mockGraphics, atLeast(2)).drawRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(mockGraphics, atLeastOnce()).drawLine(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void draw_WithBooleanFalse_DoesNotDrawCheckmark() {
        when(mockAccess.getValue(0, 0)).thenReturn("false");

        editor.startEditing(0, 0);
        editor.draw(mockGraphics, 0, 0, 10, 20, DEFAULT_VALUE_WIDTH, ColumnType.BOOLEAN, false);

        verify(mockGraphics, never()).drawLine(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void isValid_BooleanColumn_InvalidValue_ReturnsFalse() {
        when(mockAccess.getType(0)).thenReturn(ColumnType.BOOLEAN);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, 0, 'y');

        assertFalse(editor.isValid());
    }

    @Test
    void commitEdit_InvalidInteger_DoesNotSave() {
        when(mockAccess.getValue(0, 0)).thenReturn("abc");
        when(mockAccess.getType(0)).thenReturn(ColumnType.INTEGER);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        for (char c : "abc".toCharArray()) {
            editor.handleKeyEvent(0, 0, c);
        }

        editor.commitEdit();

        verify(mockAccess, never()).setValue(anyInt(), anyInt(), anyString());
        assertTrue(editor.isEditing());
    }

    @Test
    void isValid_EmailWithMultipleAts_ReturnsFalse() {
        when(mockAccess.getType(0)).thenReturn(ColumnType.EMAIL);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        for (char c : "a@b@c".toCharArray()) {
            editor.handleKeyEvent(0, 0, c);
        }

        assertFalse(editor.isValid());
    }

    @Test
    void draw_WithInvalidText_ShowsRedBorder() {
        when(mockAccess.getValue(0, 0)).thenReturn("invalid");
        when(mockAccess.getType(0)).thenReturn(ColumnType.INTEGER);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, 0, 'X');
        editor.draw(mockGraphics, 0, 0, 10, 20, DEFAULT_VALUE_WIDTH, ColumnType.INTEGER, false);

        verify(mockGraphics).setColor(Color.RED);
        verify(mockGraphics, atLeastOnce()).drawRect(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void stopEditing_ClearsEditingState() {
        when(mockAccess.getValue(0, 0)).thenReturn("data");

        editor.startEditing(0, 0);
        editor.stopEditing();

        assertFalse(editor.isEditing());
    }

    @Test
    void handleKeyEvent_BackspaceOnEmptyInput_DoesNotCrash() {
        when(mockAccess.getValue(0, 0)).thenReturn("");
        editor.startEditing(0, 0);

        editor.handleKeyEvent(0, KeyEvent.VK_BACK_SPACE, '\0');

        assertTrue(editor.isEditing());
    }

    @Test
    void isValid_String_BlankAllowed_ReturnsTrue() {
        when(mockAccess.getType(0)).thenReturn(ColumnType.STRING);
        when(mockAccess.allowsBlanks(0)).thenReturn(true);
        when(mockAccess.getValue(0, 0)).thenReturn("");

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, KeyEvent.VK_BACK_SPACE, '\0');

        assertTrue(editor.isValid());
    }

    @Test
    void isValid_IntegerWithLeadingZeros_ReturnsFalse() {
        when(mockAccess.getType(0)).thenReturn(ColumnType.INTEGER);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        for (char c : "007".toCharArray()) {
            editor.handleKeyEvent(0, 0, c);
        }

        assertFalse(editor.isValid());
    }

    @Test
    void handleKeyEvent_Enter_CommitsValidInput() {
        when(mockAccess.getValue(0, 0)).thenReturn("");
        when(mockAccess.getType(0)).thenReturn(ColumnType.STRING);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, 0, 'X');
        editor.handleKeyEvent(0, KeyEvent.VK_ENTER, '\n');

        verify(mockAccess).setValue(0, 0, "X");
        assertFalse(editor.isEditing());
    }

    @Test
    void startEditing_NullInitialValue_SetsEmptyInput() {
        when(mockAccess.getValue(0, 0)).thenReturn(null);
        when(mockAccess.getType(0)).thenReturn(ColumnType.STRING);
        when(mockAccess.allowsBlanks(0)).thenReturn(true);

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, 0, 'A');
        editor.commitEdit();

        verify(mockAccess).setValue(0, 0, "A");
    }

    @Test
    void stopEditing_ResetsInput() {
        when(mockAccess.getValue(0, 0)).thenReturn("abc");
        when(mockAccess.getType(0)).thenReturn(ColumnType.STRING);
        when(mockAccess.allowsBlanks(0)).thenReturn(true);

        editor.startEditing(0, 0);
        editor.stopEditing();

        when(mockAccess.getValue(0, 0)).thenReturn("");
        when(mockAccess.getType(0)).thenReturn(ColumnType.STRING);
        when(mockAccess.allowsBlanks(0)).thenReturn(true);

        editor.startEditing(0, 0);
        editor.commitEdit();

        verify(mockAccess, never()).setValue(anyInt(), anyInt(), eq("abc"));
    }

    @Test
    void handleKeyEvent_WhenNotEditing_DoesNothing() {
        editor.handleKeyEvent(0, 0, 'A');
        assertFalse(editor.isEditing());
    }

    @Test
    void draw_WhenNotEditing_DrawsValueNormally() {
        when(mockAccess.getValue(0, 0)).thenReturn("abc");

        editor.draw(mockGraphics, 0, 0, 10, 20, DEFAULT_VALUE_WIDTH, ColumnType.STRING, false);

        verify(mockGraphics).drawRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(mockGraphics).drawString(eq("abc"), anyInt(), anyInt());
    }

    @Test
    void isValid_Email_BlankAllowed_ReturnsTrue() {
        when(mockAccess.getType(0)).thenReturn(ColumnType.EMAIL);
        when(mockAccess.allowsBlanks(0)).thenReturn(true);

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, KeyEvent.VK_BACK_SPACE, '\0');

        assertTrue(editor.isValid());
    }

    @Test
    void isValid_Integer_BlankAllowed_ReturnsTrue() {
        when(mockAccess.getType(0)).thenReturn(ColumnType.INTEGER);
        when(mockAccess.allowsBlanks(0)).thenReturn(true);

        editor.startEditing(0, 0);

        assertTrue(editor.isValid());
    }

    @Test
    void isValid_WhenNotEditing_ReturnsTrue() {
        assertFalse(editor.isEditing());
        assertTrue(editor.isValid());
    }

    @Test
    void wasEditing_ReturnsTrueOnlyForCurrentCell() {
        when(mockAccess.getValue(2, 3)).thenReturn("data");
        editor.startEditing(2, 3);

        assertTrue(editor.wasEditing(2, 3));
        assertFalse(editor.wasEditing(1, 3));
        assertFalse(editor.wasEditing(2, 2));
    }

    @Test
    void stopEditingIfChanged_StopsEditingOnInvalidChange() {
        when(mockAccess.getValue(1, 1)).thenReturn("abc");
        when(mockAccess.getType(1)).thenReturn(ColumnType.INTEGER);
        when(mockAccess.allowsBlanks(1)).thenReturn(false);

        editor.startEditing(1, 1);
        for (char c : "abc".toCharArray()) {
            editor.handleKeyEvent(0, 0, c);
        }

        editor.stopEditingIfChanged(1, 1);

        assertFalse(editor.isEditing());
    }

    @Test
    void stopEditingIfChanged_DoesNothingIfValid() {
        when(mockAccess.getValue(0, 0)).thenReturn("123");
        when(mockAccess.getType(0)).thenReturn(ColumnType.INTEGER);
        when(mockAccess.allowsBlanks(0)).thenReturn(false);

        editor.startEditing(0, 0);
        editor.handleKeyEvent(0, 0, '1');
        editor.stopEditingIfChanged(0, 0);

        assertTrue(editor.isEditing());
    }

}
