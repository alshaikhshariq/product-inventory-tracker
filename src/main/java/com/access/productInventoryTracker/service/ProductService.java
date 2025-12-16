package com.access.productInventoryTracker.service;

import com.access.productInventoryTracker.dto.ProductDTO;
import com.access.productInventoryTracker.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.access.productInventoryTracker.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // Helper method to convert Product to ProductDTO
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getCategory().toLowerCase(),
            product.isAvailable()
        );
    }
    
    // Get all products as DTOs
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Filters products by category (case-insensitive).
     * The category is normalized to lowercase for consistent comparison with DTO transformation.
     * 
     * @param category the category to filter by (case-insensitive)
     * @return list of ProductDTO objects matching the category, empty list if category is null or not found
     */
    public List<ProductDTO> getProductsByCategory(String category) {
        return Optional.ofNullable(category)
            .map(cat -> {
                String normalizedCategory = cat.toLowerCase();
                return productRepository.findAll().stream()
                    .filter(product -> product.getCategory().equalsIgnoreCase(normalizedCategory))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            })
            .orElse(List.of());
    }

    /**
     * Filters products within a specified price range (inclusive).
     * 
     * @param minPrice the minimum price (inclusive)
     * @param maxPrice the maximum price (inclusive)
     * @return list of ProductDTO objects within the price range, empty list if minPrice > maxPrice or no products match
     */
    public List<ProductDTO> getProductsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice > maxPrice) {
            return List.of();
        }
        
        return productRepository.findAll().stream()
            .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
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
        return productRepository.findAll().stream()
            .filter(product -> product.isAvailable() == available)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

}
