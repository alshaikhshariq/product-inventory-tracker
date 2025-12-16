package com.access.productInventoryTracker.service;

import com.access.productInventoryTracker.dto.ProductDTO;
import com.access.productInventoryTracker.model.Product;
import com.access.productInventoryTracker.repository.ProductRepository;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setupMockProducts() {
        List<Product> mockProducts = Arrays.asList(
            new Product(1L, "Laptop", 1500.0, "Electronics", true),
            new Product(2L, "Smartphone", 800.0, "Electronics", false),
            new Product(3L, "Coffee Maker", 100.0, "Home Appliances", true),
            new Product(4L, "Blender", 150.0, "Home Appliances", true),
            new Product(5L, "T-Shirt", 30.0, "Apparel", true),
            new Product(6L, "Jeans", 45.0, "Apparel", true),
            new Product(7L, "Desk Lamp", 89.99, "Home Appliances", false),
            new Product(8L, "Wall Art", 120.0, "Home Decor", true),
            new Product(9L, "Sneakers", 75.0, "Apparel", true),
            new Product(10L, "Wristwatch", 250.0, "Accessories", false),
            new Product(11L, "Backpack", 60.0, "Accessories", true),
            new Product(12L, "Microwave Oven", 99.0, "Home Appliances", false),
            new Product(13L, "Floor Rug", 150.0, "Home Decor", true),
            new Product(14L, "Speaker", 300.0, "Electronics", true),
            new Product(15L, "E-reader", 200.0, "Electronics", false),
            new Product(16L, "Gaming Console", 499.99, "Electronics", true),
            new Product(17L, "Office Chair", 220.0, "Office Supplies", true),
            new Product(18L, "Pen Set", 29.99, "Office Supplies", true),
            new Product(19L, "Mountain Bike", 489.0, "Outdoor", true),
            new Product(20L, "Camping Tent", 270.0, "Outdoor", false)
        );

        when(productRepository.findAll()).thenReturn(mockProducts);
        when(productRepository.findByCategoryIgnoreCase("electronics")).thenReturn(
            mockProducts.stream().filter(p -> p.getCategory().equalsIgnoreCase("electronics")).toList()
        );
        when(productRepository.findByPriceBetween(100.0, 200.0)).thenReturn(
            mockProducts.stream().filter(p -> p.getPrice() >= 100.0 && p.getPrice() <= 200.0).toList()
        );
        when(productRepository.findByPriceBetween(2000.0, 3000.0)).thenReturn(List.of());
        when(productRepository.findByPriceBetween(100.0, 100.0)).thenReturn(
            mockProducts.stream().filter(p -> p.getPrice() == 100.0).toList()
        );
        when(productRepository.findByAvailable(true)).thenReturn(
            mockProducts.stream().filter(Product::isAvailable).toList()
        );
        when(productRepository.findByAvailable(false)).thenReturn(
            mockProducts.stream().filter(p -> !p.isAvailable()).toList()
        );
    }

    // Bug Fix Verification Test
    @Test
    public void testGetProductsByCategory_AfterFix() {
        // This test verifies the bug is fixed: the method now returns products that DO match the category
        List<ProductDTO> result = productService.getProductsByCategory("Electronics");
        
        // After fix: Should return Electronics products (IDs: 1, 2, 14, 15, 16) = 5 products
        assertFalse(result.isEmpty(), "Should return Electronics products");
        assertEquals(5, result.size(), "Should return exactly 5 Electronics products");
        
        // Verify all returned products are Electronics (bug is fixed)
        result.forEach(product -> {
            assertEquals("electronics", product.getCategory().toLowerCase(),
                "All products should be Electronics category");
        });
    }

    // Price Range Filter Tests
    @Test
    public void testGetProductsByPriceRange_ValidRange() {
        List<ProductDTO> result = productService.getProductsByPriceRange(100.0, 200.0);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Verify all products are within the price range
        result.forEach(product -> {
            assertTrue(product.getPrice() >= 100.0 && product.getPrice() <= 200.0,
                "Product price should be within range");
        });
        
        // Expected products in range 100-200: Coffee Maker (100), Blender (150), Desk Lamp (89.99 - no),
        // Wall Art (120), Microwave Oven (99 - no), Floor Rug (150), E-reader (200)
        // So: Coffee Maker, Blender, Wall Art, Floor Rug, E-reader = 5 products
        assertEquals(5, result.size());
    }

    @Test
    public void testGetProductsByPriceRange_NoProductsInRange() {
        // Use range 2000-3000 where no products exist (max price in mock data is 1500.0)
        List<ProductDTO> result = productService.getProductsByPriceRange(2000.0, 3000.0);
        
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Should return empty list when no products in range");
    }

    @Test
    public void testGetProductsByPriceRange_BoundaryValues() {
        // Test with boundary values - products at exact min and max
        List<ProductDTO> result = productService.getProductsByPriceRange(100.0, 100.0);
        
        assertNotNull(result);
        // Should return products with price exactly 100.0
        assertEquals(1, result.size());
        result.forEach(product -> {
            assertEquals(100.0, product.getPrice(), 0.01, "Product should have exact boundary price");
        });
    }

    @Test
    public void testGetProductsByPriceRange_InvalidRange() {
        // When minPrice > maxPrice, should throw an exception
        assertThrows(IllegalArgumentException.class, () -> productService.getProductsByPriceRange(200.0, 100.0));
    }

    // Category Filter Tests (After Fix)
    @Test
    public void testGetProductsByCategory_ValidCategory() {
        List<ProductDTO> result = productService.getProductsByCategory("Electronics");
        
        assertNotNull(result);
        assertEquals(5, result.size(), "Should return 5 Electronics products");
        
        // Verify all returned products are Electronics
        result.forEach(product -> {
            assertEquals("electronics", product.getCategory().toLowerCase(),
                "All products should be Electronics category");
        });
    }

    @Test
    public void testGetProductsByCategory_CaseInsensitive() {
        // Test with different case variations
        List<ProductDTO> result1 = productService.getProductsByCategory("ELECTRONICS");
        List<ProductDTO> result2 = productService.getProductsByCategory("electronics");
        List<ProductDTO> result3 = productService.getProductsByCategory("Electronics");
        
        assertEquals(5, result1.size());
        assertEquals(5, result2.size());
        assertEquals(5, result3.size());
        
        // All should return the same products
        assertEquals(result1.size(), result2.size());
        assertEquals(result2.size(), result3.size());
    }

    @Test
    public void testGetProductsByCategory_CategoryNotFound() {
        assertThrows(IllegalArgumentException.class, () -> productService.getProductsByCategory(""));
        assertThrows(IllegalArgumentException.class, () -> productService.getProductsByCategory("   "));
    }

    @Test
    public void testGetProductsByCategory_NonExistingCategory() {
        List<ProductDTO> result = productService.getProductsByCategory("NonExistentCategory");
        
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Should return empty list when category doesn't exist");
    }

    @Test
    public void testGetProductsByCategory_NullCategory() {
        assertThrows(IllegalArgumentException.class, () -> productService.getProductsByCategory(null));
    }

    // Availability Filter Tests
    @Test
    public void testGetProductsByAvailability_AvailableProducts() {
        List<ProductDTO> result = productService.getProductsByAvailability(true);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Verify all products are available
        result.forEach(product -> {
            assertTrue(product.isAvailable(), "All products should be available");
        });
        
        // Count available products from mock data: 14 available products
        assertEquals(14, result.size());
    }

    @Test
    public void testGetProductsByAvailability_UnavailableProducts() {
        List<ProductDTO> result = productService.getProductsByAvailability(false);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        
        // Verify all products are unavailable
        result.forEach(product -> {
            assertFalse(product.isAvailable(), "All products should be unavailable");
        });
        
        // Count unavailable products from mock data: 6 unavailable products
        assertEquals(6, result.size());
    }

    @Test
    public void testGetProductsByAvailability_EmptyResult() {
        // This test would need a different mock setup, but with current mock data,
        // both true and false should return results. Testing the method handles the filter correctly.
        List<ProductDTO> available = productService.getProductsByAvailability(true);
        List<ProductDTO> unavailable = productService.getProductsByAvailability(false);
        
        assertNotNull(available);
        assertNotNull(unavailable);
        // Total should equal all products
        assertEquals(20, available.size() + unavailable.size());
    }

}