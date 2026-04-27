package ui.layout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LayoutRepositoryTest {

    @Test
    void returnsSameInstanceForSameTableAndDesignView() {
        LayoutRepository repo = LayoutRepository.getInstance();

        TabularLayout layout1 = repo.forDesign("Users");
        TabularLayout layout2 = repo.forDesign("Users");

        assertSame(layout1, layout2, "Design view layout should be cached for same table");
    }

    @Test
    void returnsSameInstanceForSameTableAndRowsView() {
        LayoutRepository repo = LayoutRepository.getInstance();

        TabularLayout layout1 = repo.forRows("Orders");
        TabularLayout layout2 = repo.forRows("Orders");

        assertSame(layout1, layout2, "Rows view layout should be cached for same table");
    }

    @Test
    void returnsDifferentInstancesForDifferentTables() {
        LayoutRepository repo = LayoutRepository.getInstance();

        TabularLayout layout1 = repo.forDesign("Products");
        TabularLayout layout2 = repo.forDesign("Customers");

        assertNotSame(layout1, layout2, "Design view layouts should differ for different tables");
    }

    @Test
    void returnsDifferentInstancesForDesignAndRowsView() {
        LayoutRepository repo = LayoutRepository.getInstance();

        TabularLayout designLayout = repo.forDesign("Inventory");
        TabularLayout rowsLayout = repo.forRows("Inventory");

        assertNotSame(designLayout, rowsLayout, "Design and rows layouts for same table should be different");
    }

    @Test
    void singletonReturnsSameRepositoryInstance() {
        LayoutRepository repo1 = LayoutRepository.getInstance();
        LayoutRepository repo2 = LayoutRepository.INSTANCE;

        assertSame(repo1, repo2, "Singleton should return the same instance");
    }
}
