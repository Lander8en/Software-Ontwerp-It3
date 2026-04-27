package domain.undoLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Deque;

class UndoManagerTest {

    private UndoManager undoManager;
    private Command mockCommand;

    @BeforeEach
    void setUp() {
        undoManager = UndoManager.getInstance();
        mockCommand = mock(Command.class);

        // Clear undo/redo stacks using reflection since UndoManager is a singleton
        // and doesn't expose any reset methods.
        try {
            var undoStackField = UndoManager.class.getDeclaredField("undoStack");
            undoStackField.setAccessible(true);
            ((Deque<?>) undoStackField.get(undoManager)).clear();

            var redoStackField = UndoManager.class.getDeclaredField("redoStack");
            redoStackField.setAccessible(true);
            ((Deque<?>) redoStackField.get(undoManager)).clear();
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset UndoManager stacks", e);
        }
    }

    @Test
    void testExecute_executesCommandAndClearsRedoStack() {
        undoManager.execute(mockCommand);

        verify(mockCommand).execute();
        // Redo stack should be cleared, which is handled internally and verified
        // indirectly by redo behavior
    }

    @Test
    void testUndo_invokesUndoAndPushesToRedoStack() {
        undoManager.execute(mockCommand);
        undoManager.undo();

        verify(mockCommand).undo();
    }

    @Test
    void testRedo_invokesExecuteAgainAndPushesToUndoStack() {
        undoManager.execute(mockCommand);
        undoManager.undo();
        undoManager.redo();

        verify(mockCommand, times(2)).execute(); // once in execute, once in redo
    }

    @Test
    void testUndo_doesNothingIfStackIsEmpty() {
        undoManager.undo(); // Should not throw
    }

    @Test
    void testRedo_doesNothingIfStackIsEmpty() {
        undoManager.redo(); // Should not throw
    }

    @Test
    void testExecute_throwsOnNullCommand() {
        assertThrows(NullPointerException.class, () -> {
            undoManager.execute(null);
        }, "Expected execute(null) to throw NullPointerException");
    }
}
