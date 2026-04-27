package ui;

/**
 * Represents a keyboard shortcut consisting of a key code and optional modifier keys (Ctrl and Shift).
 *
 * <p>This class is used as a key in a map to associate key combinations with actions.</p>
 */
public class KeyCombo {

    private final int keyCode;
    private final boolean ctrl;
    private final boolean shift;

    /**
     * Constructs a KeyCombo with the specified key code and modifier flags.
     *
     * @param keyCode the key code (from KeyEvent constants)
     * @param ctrl    whether the Ctrl key is part of the combination
     * @param shift   whether the Shift key is part of the combination
     */
    public KeyCombo(int keyCode, boolean ctrl, boolean shift) {
        this.keyCode = keyCode;
        this.ctrl = ctrl;
        this.shift = shift;
    }

    /**
     * Compares this KeyCombo to another for equality.
     * Two KeyCombos are equal if they have the same key code, ctrl, and shift values.
     *
     * @param o the object to compare with
     * @return true if equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyCombo other)) return false;
        return keyCode == other.keyCode && ctrl == other.ctrl && shift == other.shift;
    }

    /**
     * Returns a hash code based on the key code and modifier flags.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int result = Integer.hashCode(keyCode);
        result = 31 * result + Boolean.hashCode(ctrl);
        result = 31 * result + Boolean.hashCode(shift);
        return result;
    }
}