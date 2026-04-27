package domain.naming;

import java.util.Collection;

/**
 * This code uses the strategy pattern to eliminate duplicate code for naming newly added tables or collumns.
 */
public interface NameGenerationStrategy {
    String generateName(String prefix, Collection<?> items);
}
