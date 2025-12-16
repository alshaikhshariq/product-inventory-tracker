package com.access.productInventoryTracker.service;

import com.access.productInventoryTracker.dto.ProductDTO;
import com.access.productInventoryTracker.model.Product;
import com.access.productInventoryTracker.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service class for managing product operations and filtering.
 * Provides methods to retrieve and filter products using database-level queries for optimal performance.
 */
@Service
public class ProductService {
    
    private final ProductRepository productRepository;

    /**
     * Constructs a ProductService with the given repository.
     * 
     * @param productRepository the repository for product data access
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Converts a Product entity to a ProductDTO.
     * Normalizes the category to lowercase for consistent data representation.
     * 
     * @param product the Product entity to convert
     * @return a ProductDTO with normalized category (lowercase)
     */
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getCategory().toLowerCase(),
            product.isAvailable()
        );
    }

    /**
     * Validates that the price range is valid.
     * Ensures both prices are non-negative and minPrice is not greater than maxPrice.
     * 
     * @param minPrice the minimum price value
     * @param maxPrice the maximum price value
     * @throws IllegalArgumentException if prices are negative or minPrice > maxPrice
     */
    private void validatePriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            throw new IllegalArgumentException("Invalid price range");
        }
    }

    /**
     * Validates and normalizes a category string.
     * Trims whitespace, checks for null/empty values, and converts to lowercase.
     * 
     * @param category the category string to validate and normalize
     * @return the normalized category (trimmed and lowercase)
     * @throws IllegalArgumentException if category is null, empty, or only whitespace
     */
    private String validateAndNormalizeCategory(String category) {
        String normalized = Optional.ofNullable(category)
            .map(String::trim)
            .filter(c -> !c.isEmpty())
            .orElseThrow(() -> new IllegalArgumentException("Category must not be null or empty"));
        return normalized.toLowerCase();
    }

    /**
     * Filters products by category (case-insensitive).
     * The category is normalized to lowercase for consistent comparison with DTO transformation.
     * 
     * @param category the category to filter by (case-insensitive)
     * @return list of ProductDTO objects matching the category, empty list if category is null or not found
     */
    public List<ProductDTO> getProductsByCategory(String category) {
        String normalizedCategory = validateAndNormalizeCategory(category);
        return productRepository.findByCategoryIgnoreCase(normalizedCategory).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Filters products within a specified price range (inclusive).
     * 
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return list of ProductDTO objects within the price range, empty list if minPrice > maxPrice or no products match
     */
    public List<ProductDTO> getProductsByPriceRange(double minPrice, double maxPrice) {
        validatePriceRange(minPrice, maxPrice);

        return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Filters products by availability status.
     * 
     * @param available the availability status to filter by
     * @return list of ProductDTO objects matching the availability status
     */
    public List<ProductDTO> getProductsByAvailability(boolean available) {
        return productRepository.findByAvailable(available).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

}
