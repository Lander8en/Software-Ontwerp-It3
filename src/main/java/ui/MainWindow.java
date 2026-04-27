package ui;

import java.awt.Graphics;

import static ui.UIConstants.BACKGROUND_COLOR;

/**
 * The main entry point and root window of the database application.
 * Delegates all rendering and input handling to the {@link SubwindowManager}.
 *
 * <p>This class extends {@link CanvasWindow} and acts as the top-level canvas where
 * all subwindows are drawn and interacted with.</p>
 */
public class MainWindow extends CanvasWindow {

    private final SubwindowManager subwindowManager;

    /**
     * Constructs the main window and initializes its subwindow manager.
     */
    public MainWindow() {
        super("Database Application");
        this.subwindowManager = new SubwindowManager();
    }

    /**
     * Paints the contents of the window by first drawing the background and then
     * delegating to the {@link SubwindowManager} to draw all active subwindows.
     *
     * @param g the Graphics context to paint with
     */
    @Override
    protected void paint(Graphics g) {
        g.setColor(BACKGROUND_COLOR);
        var bounds = g.getClipBounds();
        g.fillRect(0, 0, bounds.width, bounds.height);

        subwindowManager.drawAll(g);
    }

    /**
     * Delegates keyboard events to the {@link SubwindowManager} and repaints the window.
     */
    @Override
    protected void handleKeyEvent(int id, int keyCode, char keyChar) {
        subwindowManager.handleKeyEvent(id, keyCode, keyChar);
        repaint();
    }

    /**
     * Delegates mouse events to the {@link SubwindowManager} and repaints the window.
     */
    @Override
    protected void handleMouseEvent(int id, int x, int y, int clickCount) {
        subwindowManager.handleMouseEvent(id, x, y, clickCount);
        repaint();
    }

    /**
     * Launches the application by showing the main window on the AWT event queue.
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new MainWindow().show();
        });
    }
}