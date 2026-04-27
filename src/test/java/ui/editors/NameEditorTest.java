package ui.editors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NameEditorTest {

    private NameAccess<Object> mockAccess;
    private Graphics mockGraphics;
    private NameEditor<Object> editor;

    @BeforeEach
    void setUp() {
        mockAccess = mock(NameAccess.class); // Required, test crashes without this
        mockGraphics = mock(Graphics.class);
        editor = new NameEditor<>(mockAccess);
    }

    @Test
    void startEditing_InitializesEditingState() {
        when(mockAccess.getName(0)).thenReturn("Original");

        editor.startEditing(0);

        assertTrue(editor.isEditing());
        assertEquals(0, editor.getEditingTargetIndex());
    }

    @Test
    void handleKeyEvent_ValidCharactersAppended() {
        when(mockAccess.getName(0)).thenReturn("");
        when(mockAccess.getAll()).thenReturn(List.of(new Object()));

        editor.startEditing(0);

        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, 'a');
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, '1');
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, ' ');

        editor.commitEdit();
        verify(mockAccess).setName(0, "a1 ");
    }

    @Test
    void handleKeyEvent_BackspaceRemovesCharacters() {
        when(mockAccess.getName(0)).thenReturn("abc");
        when(mockAccess.getAll()).thenReturn(List.of(new Object()));

        editor.startEditing(0);

        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0');
        editor.commitEdit();

        verify(mockAccess).setName(0, "ab");
    }

    @Test
    void handleKeyEvent_EscapeCancelsEditing() {
        when(mockAccess.getName(0)).thenReturn("original");

        editor.startEditing(0);
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, 'X');
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ESCAPE, '\0');

        verify(mockAccess).setName(0, "original");
        assertFalse(editor.isEditing());
    }

    @Test
    void isValid_BlankInputReturnsFalse() {
        when(mockAccess.getName(0)).thenReturn("");
        when(mockAccess.getAll()).thenReturn(List.of(new Object()));

        editor.startEditing(0);
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0');

        assertFalse(editor.isValid());
    }

    @Test
    void isValid_DuplicateNameReturnsFalse() {
        Object item1 = new Object();
        Object item2 = new Object();
        when(mockAccess.getName(0)).thenReturn("Dup");
        when(mockAccess.getName(1)).thenReturn("Dup");
        when(mockAccess.getAll()).thenReturn(List.of(item1, item2));

        editor.startEditing(0);

        assertFalse(editor.isValid());
    }

    @Test
    void stopEditingIfChanged_StopsIfTargetMatches() {
        when(mockAccess.getName(0)).thenReturn("");
        when(mockAccess.getAll()).thenReturn(List.of(new Object()));

        editor.startEditing(0);
        editor.stopEditingIfChanged(0);

        assertFalse(editor.isEditing());
    }

    @Test
    void drawName_ValidAndInvalidState() {
        Object item = new Object();
        when(mockAccess.getName(0)).thenReturn("Name");
        when(mockAccess.getAll()).thenReturn(List.of(item));
        editor.startEditing(0);

        // Erase all characters
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0');
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0');
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0');
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0');

        editor.drawName(mockGraphics, 0, 10, 10, 100, 20);

        verify(mockGraphics).setColor(Color.RED);
        verify(mockGraphics).drawRect(10, 10, 100, 20);
        verify(mockGraphics).setColor(Color.BLACK);
    }

    @Test
    void handleKeyEvent_BackspaceOnEmptyInput_DoesNothing() {
        when(mockAccess.getName(0)).thenReturn("Original");
        when(mockAccess.getAll()).thenReturn(List.of(new Object()));

        editor.startEditing(0);
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0');
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_BACK_SPACE, '\0');

        assertTrue(editor.isEditing()); // Should not crash or stop editing
    }

    @Test
    void commitEdit_InvalidName_DoesNotCommit() {
        Object item1 = new Object();
        Object item2 = new Object();
        when(mockAccess.getName(0)).thenReturn("Dup");
        when(mockAccess.getName(1)).thenReturn("Dup");
        when(mockAccess.getAll()).thenReturn(List.of(item1, item2));

        editor.startEditing(0);
        editor.commitEdit();

        verify(mockAccess, never()).setName(eq(0), any());
        assertTrue(editor.isEditing());
    }

    @Test
    void cancelEdit_RestoresOriginalName() {
        when(mockAccess.getName(0)).thenReturn("Initial");
        editor.startEditing(0);

        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, 'X');
        editor.cancelEdit();

        verify(mockAccess).setName(0, "Initial");
        assertFalse(editor.isEditing());
    }

    @Test
    void stopEditingIfChanged_DoesNothingIfDifferentTarget() {
        when(mockAccess.getName(0)).thenReturn("Test");
        editor.startEditing(0);
        editor.stopEditingIfChanged(1);

        assertTrue(editor.isEditing());
    }

    @Test
    void continueEditing_SetsTargetAndEnablesEditing() {
        assertFalse(editor.isEditing());

        editor.continueEditing(0);

        assertTrue(editor.isEditing());
        assertEquals(0, editor.getEditingTargetIndex());
    }

    @Test
    void continueEditing_OverridesPreviousEditingTarget() {
        when(mockAccess.getName(0)).thenReturn("First");
        editor.startEditing(0);

        editor.continueEditing(1);

        assertEquals(1, editor.getEditingTargetIndex());
        assertTrue(editor.isEditing());
    }

    @Test
    void handleKeyEvent_DoesNothingIfNotEditing() {
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.VK_ENTER, '\0');

        assertFalse(editor.isEditing());
    }

    @Test
    void handleKeyEvent_IgnoresControlCharacters() {
        when(mockAccess.getName(0)).thenReturn("A");
        when(mockAccess.getAll()).thenReturn(List.of(new Object()));
        editor.startEditing(0);

        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, KeyEvent.CHAR_UNDEFINED);
        editor.handleKeyEvent(KeyEvent.KEY_PRESSED, 0, '\n');

        editor.commitEdit();

        verify(mockAccess).setName(0, "A"); // Should remain unchanged
    }

    @Test
    void drawName_NonEditingDrawsDefaultName() {
        when(mockAccess.getName(0)).thenReturn("StaticName");

        editor.drawName(mockGraphics, 0, 0, 0, 100, 20);

        verify(mockGraphics).setColor(Color.BLACK);
        verify(mockGraphics).drawString("StaticName", 10, 15);
    }

    @Test
    void commitEdit_NoEditingTarget_DoesNothing() {
        editor.commitEdit();
        verify(mockAccess, never()).setName(anyInt(), any());
    }

    @Test
    void cancelEdit_NoEditingTarget_DoesNothing() {
        editor.cancelEdit();
        verify(mockAccess, never()).setName(anyInt(), any());
    }
}
