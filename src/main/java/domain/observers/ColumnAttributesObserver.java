package domain.observers;

public interface ColumnAttributesObserver {
    void onColumnChanged(int colIndex);
    void onDefaultValueChanged(int colIndex);
}