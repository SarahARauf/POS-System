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
import java.util.UUID;
import java.math.BigDecimal;


public class LoyaltyMemberDAO {
    private Connection dbConnection;

    public LoyaltyMemberDAO(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // Fetch a loyalty member by ID
    public LoyaltyMember getMember(UUID memberId) {
        String query = "SELECT * FROM loyalty_members WHERE member_id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, memberId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID customerId = UUID.fromString(rs.getString("customer_id"));
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                int points = rs.getInt("points");
                BigDecimal discountRate = rs.getBigDecimal("discount_rate");

                Customer customer = new Customer(customerId, name, email, phone);
                return new LoyaltyMember(memberId, customer, points, discountRate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update loyalty member points
    public void updateMemberPoints(UUID memberId, int points) {
        String query = "UPDATE loyalty_members SET points = ? WHERE member_id = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setInt(1, points);
            stmt.setString(2, memberId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}