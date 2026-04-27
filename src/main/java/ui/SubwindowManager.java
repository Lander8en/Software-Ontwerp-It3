package ui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.Table;
import domain.observers.TableRemovalObserver;
import domain.undoLogic.UndoManager;
import ui.controllers.TableController;
import ui.controllers.TableRepoController;
import ui.interaction.MouseInteractionDispatcher;

/**
 * Manages all open subwindows and handles input dispatching.
 * 
 * <p>Acts as a centralized controller for mouse and keyboard input routing,
 * subwindow creation and lifecycle management.</p>
 */
public class SubwindowManager implements TableRemovalObserver {

    private final TableRepoController tableRepoController = new TableRepoController();
    private final List<Subwindow> subwindows = new ArrayList<>();
    private final SubwindowFactory factory;
    private final MouseInteractionDispatcher mouseDispatcher;
    private final Map<KeyCombo, Runnable> keyCommands = new HashMap<>();
    private boolean ctrlDown = false;
    private boolean shiftDown = false;

    /**
     * Initializes a SubwindowManager with a default SubwindowFactory
     * and sets up key command mappings.
     */
    public SubwindowManager() {
        this.factory = new SubwindowFactory();
        this.mouseDispatcher = new MouseInteractionDispatcher();
        tableRepoController.addTableRemovalObserver(this);
        setupkeyCommands();
    }

    /**
     * Configures global key combinations and their associated actions.
     */
    private void setupkeyCommands() {
        keyCommands.put(new KeyCombo(KeyEvent.VK_T, true, false), this::addNewTablesSubwindow);
        keyCommands.put(new KeyCombo(KeyEvent.VK_ENTER, true, false), this::handleCtrlEnter);
        keyCommands.put(new KeyCombo(KeyEvent.VK_F, true, false), this::handleCtrlF);
        keyCommands.put(new KeyCombo(KeyEvent.VK_Z, true, false), () -> UndoManager.getInstance().undo());
        keyCommands.put(new KeyCombo(KeyEvent.VK_Z, true, true), () -> UndoManager.getInstance().redo());
    }

    /**
     * Opens the alternative view (design ↔ rows) for the currently active table subwindow.
     */
    private void handleCtrlEnter() {
        if (subwindows.isEmpty()) return;

        Subwindow active = subwindows.get(subwindows.size() - 1);

        if (active instanceof TableDesignSubwindow designWindow) {
            TableController controller = designWindow.getController();
            addNewTableRowsSubwindow(controller);
        } else if (active instanceof TableRowsSubwindow rowsWindow) {
            TableController controller = rowsWindow.getController();
            addNewTableDesignSubwindow(controller);
        }
    }

    /**
     * Opens a form subwindow for the currently selected table in the tables view.
     */
    private void handleCtrlF() {
        if (subwindows.isEmpty()) return;

        Subwindow active = subwindows.get(subwindows.size() - 1);
        if (active instanceof TablesSubwindow tablesSubwindow) {
            int tableIndex = tablesSubwindow.getSelectedTableIndex();
            TableController controller = tablesSubwindow.getTableController(tableIndex);
            addNewFormSubwindow(controller);
        }
    }

    /**
     * Opens a new {@link TablesSubwindow} listing all tables.
     */
    public void addNewTablesSubwindow() {
        TablesSubwindow newWindow = factory.createNewTableSubwindow(tableRepoController);
        subwindows.add(newWindow);
        tableRepoController.addTableNameChangeObserver(newWindow);
    }

    /**
     * Opens a new {@link TableDesignSubwindow} for a given table.
     */
    public void addNewTableDesignSubwindow(TableController controller) {
        TableDesignSubwindow newWindow = factory.createNewTableDesignSubwindow(controller);
        subwindows.add(newWindow);
        tableRepoController.addTableNameChangeObserver(newWindow);
    }

    /**
     * Opens a new {@link TableRowsSubwindow} for a given table.
     */
    public void addNewTableRowsSubwindow(TableController controller) {
        TableRowsSubwindow newWindow = factory.createNewTableRowsSubwindow(controller);
        subwindows.add(newWindow);
        tableRepoController.addTableNameChangeObserver(newWindow);
    }

    /**
     * Opens a new {@link FormSubwindow} for a given table.
     */
    public void addNewFormSubwindow(TableController controller) {
        FormSubwindow newWindow = factory.createNewFormSubwindow(controller);
        subwindows.add(newWindow);
    }

    /**
     * Draws all subwindows, highlighting the active one.
     */
    public void drawAll(Graphics g) {
        for (int i = 0; i < subwindows.size(); i++) {
            Subwindow w = subwindows.get(i);
            boolean isActive = (i == subwindows.size() - 1);
            w.draw(g, isActive);
        }
    }

    /**
     * Handles key input and delegates to the active subwindow or triggers commands.
     */
    public void handleKeyEvent(int id, int keyCode, char keyChar) { 
        updateDownKeys(id, keyCode);

        Runnable command = keyCommands.get(new KeyCombo(keyCode, ctrlDown, shiftDown));

        Subwindow currentWindow = subwindows.isEmpty() ? null : subwindows.get(subwindows.size() - 1);

        if (command != null && id == KeyEvent.KEY_PRESSED) {
            command.run();
        }

        if (currentWindow != null) {
            currentWindow.handleKeyEvent(id, keyCode, keyChar);
        }
    }

    /**
     * Tracks whether control or shift are held down for command detection.
     */
    private void updateDownKeys(int id, int keyCode) {
        if (keyCode == KeyEvent.VK_CONTROL) {
            ctrlDown = id == KeyEvent.KEY_PRESSED || (ctrlDown && id != KeyEvent.KEY_RELEASED);
        }
        if (keyCode == KeyEvent.VK_SHIFT) {
            shiftDown = id == KeyEvent.KEY_PRESSED || (shiftDown && id != KeyEvent.KEY_RELEASED);
        }
    }

    /**
     * Delegates a mouse event to the appropriate strategy for the target subwindow.
     */
    public void handleMouseEvent(int id, int x, int y, int clickCount) {
        Subwindow target = findSubwindowAt(x, y);
        if (target == null) return;

        bringToFront(target);
        mouseDispatcher.dispatchMouseEvent(this, target, id, x, y, clickCount);
    }

    /**
     * Brings the given subwindow to the front (top of the z-order).
     */
    public void bringToFront(Subwindow subwindow) {
        if (subwindow != null && subwindows.remove(subwindow)) {
            subwindows.add(subwindow);
        }
    }

    /**
     * Finds the topmost subwindow under the given coordinates.
     */
    public Subwindow findSubwindowAt(int x, int y) {
        for (int i = subwindows.size() - 1; i >= 0; i--) {
            Subwindow w = subwindows.get(i);
            if (w.contains(x, y)) {
                return w;
            }
        }
        return null;
    }

    /**
     * Closes and removes the given subwindow.
     */
    public void closeSubwindow(Subwindow w) {
        subwindows.remove(w);
    }

    /**
     * Triggered when a table is deleted: closes all subwindows linked to it.
     */
    @Override
    public void onTableRemoved(Table table) {
        closeAllSubwindowsForTable(table);
    }

    /**
     * Closes all subwindows that belong to a given table.
     *
     * @param table the table whose windows should be closed
     */
    public void closeAllSubwindowsForTable(Table table) {
        String tableName = table.getName();
        subwindows.removeIf(w -> tableName.equals(w.getTableName()));
    }
}