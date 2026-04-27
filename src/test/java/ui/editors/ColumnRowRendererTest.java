package ui.editors;

import domain.Column;
import domain.ColumnType;
import ui.controllers.TableController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.mockito.Mockito.*;
import static ui.UIConstants.DEFAULT_VALUE_WIDTH;

public class ColumnRowRendererTest {

    private NameEditor<Column> mockNameEditor;
    private ColumnDefaultValueEditor mockDefaultValueEditor;
    private TableController mockController;
    private Column mockColumn;
    private Graphics mockGraphics;
    private ColumnRowRenderer renderer;

    @BeforeEach
    void setUp() {
        mockNameEditor = mock(NameEditor.class); // Required, test crashes without this
        mockDefaultValueEditor = mock(ColumnDefaultValueEditor.class);
        mockController = mock(TableController.class);
        mockColumn = mock(Column.class);
        mockGraphics = mock(Graphics.class);

        renderer = new ColumnRowRenderer(mockNameEditor, mockDefaultValueEditor, mockController);
    }

    @Test
    void drawRow_DrawsSelectedRowBackground() {
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);
        when(mockDefaultValueEditor.isEditing()).thenReturn(false);

        renderer.drawRow(mockGraphics, 0, mockColumn, 10, 20, true, DEFAULT_VALUE_WIDTH);

        verify(mockGraphics).setColor(Color.LIGHT_GRAY);
        verify(mockGraphics).fillRect(10, 20, 200, 20);
    }

    @Test
    void drawRow_DelegatesNameDrawing() {
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);
        when(mockDefaultValueEditor.isEditing()).thenReturn(false);

        renderer.drawRow(mockGraphics, 0, mockColumn, 0, 0, false, DEFAULT_VALUE_WIDTH);

        verify(mockNameEditor).drawName(mockGraphics, 0, 0, 0, 200, 20);
    }

    @Test
    void drawRow_DrawsTypeText() {
        when(mockColumn.getType()).thenReturn(ColumnType.EMAIL);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);
        when(mockDefaultValueEditor.isEditing()).thenReturn(false);

        renderer.drawRow(mockGraphics, 0, mockColumn, 0, 0, false, DEFAULT_VALUE_WIDTH);

        verify(mockGraphics).drawString("[EMAIL]", 215, 15);
    }

    @Test
    void drawRow_DrawsBlanksAllowedCheckbox_Unchecked() {
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(false);
        when(mockColumn.getDefaultValue()).thenReturn("someValue");
        when(mockDefaultValueEditor.isEditing()).thenReturn(false);

        renderer.drawRow(mockGraphics, 0, mockColumn, 0, 0, false, DEFAULT_VALUE_WIDTH);

        // Updated expected X coordinate to match the new layout logic
        int expectedCheckboxX = 0 + 5 + 100 + 210; // rowX + SPACER + TYPE_WIDTH + OFFSET
        int checkboxY = 3;
        int checkboxSize = 14;

        verify(mockGraphics).fillRect(expectedCheckboxX, checkboxY, checkboxSize, checkboxSize);
        verify(mockGraphics).drawRect(expectedCheckboxX, checkboxY, checkboxSize, checkboxSize);
    }

    @Test
    void drawRow_HighlightsCheckboxIfBlankViolation() {
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.getDefaultValue()).thenReturn("   "); // Blank default
        when(mockColumn.isBlanksAllowed()).thenReturn(false); // Blanks not allowed
        when(mockDefaultValueEditor.isEditing()).thenReturn(false);
        when(mockDefaultValueEditor.isValid(mockColumn)).thenReturn(true);

        renderer.drawRow(mockGraphics, 0, mockColumn, 0, 0, false, DEFAULT_VALUE_WIDTH);

        verify(mockGraphics, atLeastOnce()).setColor(Color.RED);
        verify(mockGraphics).drawRect(314, 2, 16, 16);
    }

    @Test
    void drawRow_DelegatesDefaultValueEditor_WhenEditing() {
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockDefaultValueEditor.isEditing()).thenReturn(true);
        when(mockDefaultValueEditor.getEditingTargetIndex()).thenReturn(0);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);

        renderer.drawRow(mockGraphics, 0, mockColumn, 0, 0, false, DEFAULT_VALUE_WIDTH);

        verify(mockDefaultValueEditor).drawValue(mockGraphics, 0, mockColumn, 365, 0, DEFAULT_VALUE_WIDTH);
    }

    @Test
    void drawRow_DrawsBooleanDefaultValue() {
        when(mockColumn.getType()).thenReturn(ColumnType.BOOLEAN);
        when(mockColumn.getDefaultValue()).thenReturn("true");
        when(mockColumn.isBlanksAllowed()).thenReturn(true);
        when(mockDefaultValueEditor.isEditing()).thenReturn(false);

        renderer.drawRow(mockGraphics, 0, mockColumn, 0, 0, false, DEFAULT_VALUE_WIDTH);

        verify(mockGraphics).drawLine(370, 10, 373, 13);
        verify(mockGraphics).drawLine(373, 13, 378, 7);
    }

    @Test
    void drawRow_HighlightsInvalidDefaultValue() {
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.getDefaultValue()).thenReturn("invalid");
        when(mockDefaultValueEditor.isEditing()).thenReturn(false);
        when(mockDefaultValueEditor.isValid(mockColumn)).thenReturn(false);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);

        renderer.drawRow(mockGraphics, 0, mockColumn, 0, 0, false, DEFAULT_VALUE_WIDTH);

        verify(mockGraphics).setColor(Color.RED);
        verify(mockGraphics).drawRect(364, 0, 100, 19);
    }

    @Test
    void drawRow_MarksTypeBlockedColumn() {
        when(mockColumn.getType()).thenReturn(ColumnType.STRING);
        when(mockColumn.isBlanksAllowed()).thenReturn(true);
        when(mockDefaultValueEditor.isTypeBlocked(0)).thenReturn(true); // Blocked type
        when(mockDefaultValueEditor.isEditing()).thenReturn(false);
        when(mockDefaultValueEditor.isValid(mockColumn)).thenReturn(true);

        renderer.drawRow(mockGraphics, 0, mockColumn, 0, 0, false, DEFAULT_VALUE_WIDTH);

        verify(mockGraphics).setColor(Color.RED);
        verify(mockGraphics).drawRect(213, 0, 80, 18);
    }

}
