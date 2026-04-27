package domain.undoLogic;

/**
 * Represents a reversible operation that can be executed and undone.
 * Used to implement undo/redo functionality throughout the application.
 */
public interface Command {

    /**
     * Executes the command.
     * This method performs the action associated with the command.
     */
    void execute();

    /**
     * Undoes the command.
     * This method reverts the changes made by the {@code execute()} method.
     */
    void undo();
}