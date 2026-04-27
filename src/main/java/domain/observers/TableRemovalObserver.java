package domain.observers;

import domain.Table;

public interface TableRemovalObserver {
    void onTableRemoved(Table table);
}
