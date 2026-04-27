package domain.undoLogic;

import domain.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BlanksAllowedCommandTest {

    private Table mockTable;
    private BlanksAllowedCommand command;

    @BeforeEach
    void setUp() {
        mockTable = mock(Table.class);
        command = new BlanksAllowedCommand(mockTable, 1); // using column index 1
    }

    @Test
    void testExecute_togglesBlanksAllowed() {
        command.execute();
        verify(mockTable).toggleBlanksAllowed(1);
    }

    @Test
    void testUndo_togglesBlanksAllowedAgain() {
        command.undo();
        verify(mockTable).toggleBlanksAllowed(1);
    }

    @Test
    void testConstructorThrowsOnNegativeIndex() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BlanksAllowedCommand(mockTable, -1);
        }, "Expected constructor to throw for negative index");
    }
}
