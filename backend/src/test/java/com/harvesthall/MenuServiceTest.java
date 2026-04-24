package com.harvesthall;

import com.harvesthall.model.MenuItem;
import com.harvesthall.repository.MenuItemRepository;
import com.harvesthall.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MenuService.
 * Uses Mockito to mock the repository so no DB is needed.
 *
 * XP Test-First: these tests were written alongside the service code.
 */
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @InjectMocks
    private MenuService menuService;

    private MenuItem burger;
    private MenuItem drink;

    @BeforeEach
    void setUp() {
        burger = new MenuItem("All Beef Burger", "Burgers", 8.99, "Classic beef burger");
        burger.setId(1L);

        drink = new MenuItem("Fountain Drink", "Drinks", 2.49, "Pepsi, OJ, or 7UP");
        drink.setId(2L);
    }

    // ----------------------------------------------------------------
    // Test Case 1: Menu Display
    // ----------------------------------------------------------------

    @Test
    @DisplayName("TC-01: getAvailableMenuItems returns all available items")
    void testGetAvailableMenuItems() {
        when(menuItemRepository.findByAvailableTrue()).thenReturn(List.of(burger, drink));

        List<MenuItem> result = menuService.getAvailableMenuItems();

        assertNotNull(result, "Result list should not be null");
        assertEquals(2, result.size(), "Should return 2 menu items");
        assertTrue(result.stream().anyMatch(i -> i.getName().equals("All Beef Burger")));
        verify(menuItemRepository, times(1)).findByAvailableTrue();
    }

    @Test
    @DisplayName("TC-01b: getAvailableMenuItems returns empty list when no items available")
    void testGetAvailableMenuItemsEmpty() {
        when(menuItemRepository.findByAvailableTrue()).thenReturn(List.of());

        List<MenuItem> result = menuService.getAvailableMenuItems();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Empty menu should return empty list, not null");
    }

    @Test
    @DisplayName("TC-01c: getMenuItemsByCategory filters correctly")
    void testGetMenuItemsByCategory() {
        when(menuItemRepository.findByCategoryAndAvailableTrue("Burgers"))
            .thenReturn(List.of(burger));

        List<MenuItem> result = menuService.getMenuItemsByCategory("Burgers");

        assertEquals(1, result.size());
        assertEquals("Burgers", result.get(0).getCategory());
    }

    // ----------------------------------------------------------------
    // Story 5 (Release 2): Admin menu management
    // ----------------------------------------------------------------

    @Test
    @DisplayName("TC-05a: addMenuItem saves and returns new item")
    void testAddMenuItem() {
        MenuItem newItem = new MenuItem("Onion Rings", "Sides", 3.99, "Beer-battered rings");
        when(menuItemRepository.save(newItem)).thenReturn(newItem);

        MenuItem saved = menuService.addMenuItem(newItem);

        assertNotNull(saved);
        assertEquals("Onion Rings", saved.getName());
        verify(menuItemRepository, times(1)).save(newItem);
    }

    @Test
    @DisplayName("TC-05b: updateMenuItem changes fields correctly")
    void testUpdateMenuItem() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(burger));
        MenuItem updates = new MenuItem("Premium Beef Burger", "Burgers", 10.99, "Upgraded burger");
        updates.setAvailable(true);
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(i -> i.getArguments()[0]);

        MenuItem result = menuService.updateMenuItem(1L, updates);

        assertEquals("Premium Beef Burger", result.getName());
        assertEquals(10.99, result.getPrice());
    }

    @Test
    @DisplayName("TC-05c: updateMenuItem throws when item not found")
    void testUpdateMenuItemNotFound() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());
        MenuItem updates = new MenuItem("Ghost Item", "Sides", 1.00, "");

        assertThrows(RuntimeException.class, () -> menuService.updateMenuItem(99L, updates));
    }

    @Test
    @DisplayName("TC-05d: removeMenuItem soft-deletes (sets available=false)")
    void testRemoveMenuItem() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(burger));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(i -> i.getArguments()[0]);

        menuService.removeMenuItem(1L);

        assertFalse(burger.getAvailable(), "Item should be marked unavailable after removal");
        verify(menuItemRepository, times(1)).save(burger);
    }
}
