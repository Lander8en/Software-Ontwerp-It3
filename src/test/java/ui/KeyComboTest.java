package ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyComboTest {

    @Test
    void equals_ReturnsTrue_WhenAllFieldsMatch() {
        KeyCombo combo1 = new KeyCombo(65, true, false);
        KeyCombo combo2 = new KeyCombo(65, true, false);

        assertEquals(combo1, combo2);
        assertEquals(combo1.hashCode(), combo2.hashCode());
    }

    @Test
    void equals_ReturnsFalse_WhenKeyCodeDiffers() {
        KeyCombo combo1 = new KeyCombo(65, true, false);
        KeyCombo combo2 = new KeyCombo(66, true, false);

        assertNotEquals(combo1, combo2);
    }

    @Test
    void equals_ReturnsFalse_WhenCtrlDiffers() {
        KeyCombo combo1 = new KeyCombo(65, true, false);
        KeyCombo combo2 = new KeyCombo(65, false, false);

        assertNotEquals(combo1, combo2);
    }

    @Test
    void equals_ReturnsFalse_WhenShiftDiffers() {
        KeyCombo combo1 = new KeyCombo(65, true, false);
        KeyCombo combo2 = new KeyCombo(65, true, true);

        assertNotEquals(combo1, combo2);
    }

    @Test
    void equals_ReturnsFalse_WhenObjectIsNull() {
        KeyCombo combo = new KeyCombo(65, true, false);

        assertNotEquals(combo, null);
    }

    @Test
    void equals_ReturnsFalse_WhenOtherIsDifferentType() {
        KeyCombo combo = new KeyCombo(65, true, false);

        assertNotEquals(combo, "NotAKeyCombo");
    }

    @Test
    void hashCode_IsConsistentAcrossCalls() {
        KeyCombo combo = new KeyCombo(42, true, true);
        int hash1 = combo.hashCode();
        int hash2 = combo.hashCode();

        assertEquals(hash1, hash2);
    }

    @Test
    void hashCode_Differs_WhenShiftDiffers() {
        KeyCombo combo1 = new KeyCombo(42, true, true);
        KeyCombo combo2 = new KeyCombo(42, true, false);

        assertNotEquals(combo1.hashCode(), combo2.hashCode());
    }

    @Test
    void hashCode_Differs_WhenCtrlDiffers() {
        KeyCombo combo1 = new KeyCombo(42, true, false);
        KeyCombo combo2 = new KeyCombo(42, false, false);

        assertNotEquals(combo1.hashCode(), combo2.hashCode());
    }

    @Test
    void hashCode_Differs_WhenKeyCodeDiffers() {
        KeyCombo combo1 = new KeyCombo(42, true, false);
        KeyCombo combo2 = new KeyCombo(43, true, false);

        assertNotEquals(combo1.hashCode(), combo2.hashCode());
    }
}
