package com.access.productInventoryTracker.repository;

import com.access.productInventoryTracker.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // You can add custom methods here if needed, for example: 
    // List<Product> findByCategory(String category);


    // Derived queries for efficient filtering
    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    List<Product> findByAvailable(boolean available);
}
