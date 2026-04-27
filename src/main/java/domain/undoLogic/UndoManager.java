package domain.undoLogic;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Singleton manager responsible for tracking and executing undoable commands.
 * 
 * Supports standard undo and redo functionality using two stacks:
 * - One for executed commands (undo stack)
 * - One for undone commands (redo stack)
 */
public class UndoManager {

    /** The single instance of the UndoManager (singleton pattern). */
    private static final UndoManager instance = new UndoManager();

    /** Stack holding commands that can be undone. */
    private final Deque<Command> undoStack = new ArrayDeque<>();

    /** Stack holding commands that can be redone. */
    private final Deque<Command> redoStack = new ArrayDeque<>();

    /** Private constructor to enforce singleton pattern. */
    private UndoManager() {}

    /**
     * Returns the single instance of the UndoManager.
     *
     * @return the UndoManager instance
     */
    public static UndoManager getInstance() {
        return instance;
    }

    /**
     * Executes the given command and adds it to the undo history.
     * Clears the redo history as a new action invalidates redoable state.
     *
     * @param command the command to execute (must not be null)
     * @throws NullPointerException if the command is null
     */
    public void execute(Command command) {
        if (command == null) {
            throw new NullPointerException("Command must not be null");
        }
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Undoes the most recent command, if available.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    /**
     * Redoes the most recently undone command, if available.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
}