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
import java.sql.SQLException;
import java.util.UUID;

public class SaleDAO {
    private Connection dbConnection;

    public SaleDAO(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Save a sale to the database
    public void saveSale(Sale sale) {
        String query = "INSERT INTO sales (sale_id, total_amount, discount_amount, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, sale.getSaleId().toString());
            stmt.setBigDecimal(2, sale.getTotalAmount());
            stmt.setBigDecimal(3, sale.getLoyaltyDiscount());
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(sale.getTimestamp()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}