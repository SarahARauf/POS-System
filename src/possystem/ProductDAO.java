/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package possystem;

/**
 *
 * @author Sarah
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

public class ProductDAO {
    private Connection dbConnection;

    public ProductDAO(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Fetch all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UUID productId = UUID.fromString(rs.getString("product_id"));
                String name = rs.getString("name");
                BigDecimal price = rs.getBigDecimal("price");
                int stockQuantity = rs.getInt("stock_quantity");
                String imgPath = rs.getString("img_path");
                products.add(new Product(productId, name, price, stockQuantity, imgPath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Fetch a product by ID
    public Product getProduct(UUID productId) {
        String query = "SELECT * FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, productId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Product(
                    UUID.fromString(rs.getString("product_id")),
                    rs.getString("name"),
                    rs.getBigDecimal("price"),
                    rs.getInt("stock_quantity"),
                    rs.getString("img_path")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update stock quantity
    public void updateStock(UUID productId, int quantity) {
        String query = "UPDATE products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, productId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}