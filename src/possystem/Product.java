/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package possystem;

/**
 *
 * @author Sarah
 */

import java.math.BigDecimal;
import java.util.UUID;

/*
Product model

Outlines stockQuantity and unit price
*/

public class Product {
    private UUID productId;
    private String name;
    private BigDecimal price;
    private int stockQuantity;

    public Product(UUID productId, String name, BigDecimal price, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // Getters
    public UUID getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    // Business logic: Update stock quantity
    public void updateStock(int quantity) {
        if (this.stockQuantity + quantity < 0) {
            throw new IllegalArgumentException("Insufficient stock.");
        }
        this.stockQuantity += quantity;
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %s) - $%.2f | Stock: %d", name, productId, price, stockQuantity);
    }
}