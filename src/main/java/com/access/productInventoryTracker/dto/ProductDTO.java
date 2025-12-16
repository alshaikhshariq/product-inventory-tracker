package com.access.productInventoryTracker.dto;

/**
 * Data Transfer Object (DTO) for Product entities.
 * Used to transfer product data between layers without exposing entity details.
 * Category is stored in lowercase for consistency.
 */
public class ProductDTO {
    private Long id;
    private String name;
    private double price;
    private String category;
    private boolean available;

    /**
     * Constructs a ProductDTO with the specified values.
     * 
     * @param id the product ID
     * @param name the product name
     * @param price the product price
     * @param category the product category (should be lowercase for consistency)
     * @param available the availability status
     */
    public ProductDTO(Long id, String name, double price, String category, boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }
}
