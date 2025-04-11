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

public class LoyaltyMember extends Customer {
    private UUID memberId;
    private int points; //currently, points doesn't do anything, might remove it.
    private BigDecimal discountRate;

    public LoyaltyMember(UUID memberId, Customer customer, int points, BigDecimal discountRate) {
        super(customer.getCustomerId(), customer.getName(), customer.getEmail(), customer.getPhone());
        this.memberId = memberId;
        this.points = points;
        this.discountRate = discountRate;
    }

    // Apply member discount
    public BigDecimal applyMemberDiscount() {
        return discountRate;
    }

//    // Add points
//    public void addPoints(int points) {
//        this.points += points;
//    }

    // Getters
    public UUID getMemberId() {
        return memberId;
    }

    public int getPoints() {
        return points;
    }

//    public BigDecimal getDiscountRate() {
//        return discountRate;
//    }
}