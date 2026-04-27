package ui.editors;

import java.util.List;

/**
 * Generic interface for accessing and modifying the names of a list of items.
 *
 * @param <T> the type of items being named (e.g., Table, Column)
 */
public interface NameAccess<T> {

    /**
     * Returns the name of the item at the specified index.
     *
     * @param index the index of the item
     * @return the name of the item
     */
    String getName(int index);

    /**
     * Updates the name of the item at the specified index.
     *
     * @param index the index of the item
     * @param name the new name to assign
     */
    void setName(int index, String name);

    /**
     * Returns a list of all items being managed.
     *
     * @return a list of items
     */
    List<T> getAll();
}