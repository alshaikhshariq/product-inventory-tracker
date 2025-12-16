package com.access.productInventoryTracker.repository;

import com.access.productInventoryTracker.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Product entity operations.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 * Uses Spring Data JPA derived query methods for efficient database-level filtering.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Finds all products matching the given category (case-insensitive).
     * Uses database-level filtering for optimal performance.
     * 
     * @param category the category to search for (case-insensitive)
     * @return a list of products matching the category
     */
    List<Product> findByCategoryIgnoreCase(String category);

    /**
     * Finds all products within the specified price range (inclusive).
     * Uses database-level filtering for optimal performance.
     * 
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return a list of products within the price range
     */
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    /**
     * Finds all products matching the given availability status.
     * Uses database-level filtering for optimal performance.
     * 
     * @param available the availability status to filter by
     * @return a list of products matching the availability status
     */
    List<Product> findByAvailable(boolean available);
}
