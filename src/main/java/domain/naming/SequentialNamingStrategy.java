package domain.naming;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default sequential naming strategy that appends an incrementing number
 * to a given prefix until a unique name is found.
 *
 * <p>This strategy assumes that all objects in the collection have a public
 * {@code getName()} method that returns a {@code String}.</p>
 */
public class SequentialNamingStrategy implements NameGenerationStrategy {

    /**
     * Generates a unique name by appending a number to the given prefix,
     * skipping any names already present in the collection.
     *
     * @param prefix the name prefix (e.g., "Table", "Column"); must not be null
     * @param items  the collection of existing named objects; must not be null
     * @return a unique name not yet used in the collection
     * @throws RuntimeException if any item in the collection does not implement {@code getName()}
     *                          or if reflection fails
     */
    @Override
    public String generateName(String prefix, Collection<?> items) {
        Set<String> existingNames = items.stream()
            .map(item -> {
                try {
                    return (String) item.getClass().getMethod("getName").invoke(item);
                } catch (Exception e) {
                    throw new RuntimeException("All items must have getName()", e);
                }
            })
            .collect(Collectors.toSet());

        int i = 1;
        while (existingNames.contains(prefix + i)) {
            i++;
        }
        return prefix + i;
    }
}