package domain.nameGenerating;

import org.junit.jupiter.api.Test;

import domain.naming.NameGenerationStrategy;
import domain.naming.NameGenerator;
import domain.naming.SequentialNamingStrategy;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Collection;

public class NameGeneratorTest {

    // Test class mimicking Column class
    public static class TestEntity {
        private final String name;

        public TestEntity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    void generatesSequentialNames() {
        // Setup
        Collection<TestEntity> existingItems = new ArrayList<>();
        existingItems.add(new TestEntity("Column1"));
        existingItems.add(new TestEntity("Column2"));

        NameGenerationStrategy strategy = new SequentialNamingStrategy();
        NameGenerator generator = new NameGenerator(strategy);

        // Test
        String newName = generator.generateUniqueName("Column", existingItems);

        // Verify
        assertEquals("Column3", newName);
    }

    @Test
    void startsAt1WhenEmpty() {
        NameGenerator generator = new NameGenerator(new SequentialNamingStrategy());
        String name = generator.generateUniqueName("Table", new ArrayList<>());
        assertEquals("Table1", name);
    }

    @Test
    void handlesGapsInSequence() {
        Collection<TestEntity> items = new ArrayList<>();
        items.add(new TestEntity("Row1"));
        items.add(new TestEntity("Row3")); // Gap at 2

        String name = new NameGenerator(new SequentialNamingStrategy())
                .generateUniqueName("Row", items);

        assertEquals("Row2", name); // Should fill the gap
    }

    @Test
    void throwsWhenMissingGetName() {
        Collection<Object> invalidItems = new ArrayList<>();
        invalidItems.add(new Object()); // No getName() method

        assertThrows(RuntimeException.class, () -> {
            new NameGenerator(new SequentialNamingStrategy())
                    .generateUniqueName("Test", invalidItems);
        });
    }
}