package domain.observers;

import domain.Table;

public interface TableNameChangeObserver {
    void onTableNameChanged(Table table);
}
