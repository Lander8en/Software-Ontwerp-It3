package domain.naming;

import java.util.Collection;
import java.util.Objects;

/**
 * Utility class that uses a {@link NameGenerationStrategy} to generate
 * unique names based on a prefix and an existing collection of items.
 */
public class NameGenerator {

    private final NameGenerationStrategy strategy;

    /**
     * Constructs a NameGenerator with the specified strategy.
     *
     * @param strategy the name generation strategy to use; must not be null
     * @throws NullPointerException if the strategy is null
     */
    public NameGenerator(NameGenerationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy must not be null");
    }

    /**
     * Generates a unique name using the provided prefix and collection of items.
     *
     * @param prefix the prefix to use for name generation; must not be null
     * @param items  the collection of existing items to check uniqueness against; must not be null
     * @return a unique name based on the prefix
     * @throws NullPointerException if prefix or items is null
     */
    public String generateUniqueName(String prefix, Collection<?> items) {
        Objects.requireNonNull(prefix, "prefix must not be null");
        Objects.requireNonNull(items, "items must not be null");
        return strategy.generateName(prefix, items);
    }
}