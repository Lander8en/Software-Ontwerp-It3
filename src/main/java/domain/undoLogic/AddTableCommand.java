package domain.undoLogic;

import java.util.List;
import java.util.Objects;

import domain.Table;
import domain.TableRepository;
import ui.controllers.TableController;

/**
 * Command representing the creation and addition of a new table to a repository.
 * This command also adds a corresponding TableController to the provided list.
 */
public class AddTableCommand implements Command {

    private final TableRepository repo;
    private final List<TableController> controllers;
    private final int index;
    private final Table table;

    /**
     * Constructs an AddTableCommand which adds a new table to the repository
     * and a corresponding controller to the given list.
     *
     * @param repo         the table repository to add the new table to; must not be null
     * @param controllers  the list of controllers to which a new controller will be added; must not be null
     * @throws NullPointerException if repo or controllers is null
     */
    public AddTableCommand(TableRepository repo, List<TableController> controllers) {
        this.repo = Objects.requireNonNull(repo, "repo must not be null");
        this.controllers = Objects.requireNonNull(controllers, "controllers must not be null");
        this.index = repo.getTablesCount();
        this.table = repo.createNewTable();
    }

    /**
     * Executes the command by adding the new table to the repository
     * and adding a corresponding controller to the list.
     */
    @Override
    public void execute() {
        repo.addTable(index, table);
        controllers.add(new TableController(table));
    }

    /**
     * Undoes the command by removing the table from the repository
     * and removing the corresponding controller from the list.
     */
    @Override
    public void undo() {
        repo.remove(table);
        controllers.remove(index);
    }
}