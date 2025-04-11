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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.math.RoundingMode;

/*
Sale model
This model represents the "cart", which includes the list of SaleItem and calculates the total amount of all items in the cart
It will also apply a loyaltyDiscount, if loyaltyDiscount is not 0.
*/

public class Sale {

    private UUID saleId;
    private List<SaleItem> items;
    private BigDecimal totalAmount;
    private BigDecimal loyaltyDiscount;
    private LocalDateTime timestamp;

    public Sale() {
        this.saleId = UUID.randomUUID();
        this.items = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.loyaltyDiscount = BigDecimal.ZERO;
        this.timestamp = LocalDateTime.now();
    }

    // Add item to sale
    public void addItem(SaleItem item) {
        items.add(item);
        calculateTotal();
        System.out.println(items);
    }
    
    
    // Remove item from sale
    public void removeItem(SaleItem item) {
        items.remove(item);
        calculateTotal();
    }
    

    // Calculate total amount
    public void calculateTotal() {
//        System.out.println("Loyalty Discount: " + loyaltyDiscount);

        // Calculate the total amount before discount
        totalAmount = items.stream()
                .map(SaleItem::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply the loyalty discount
        if (loyaltyDiscount.compareTo(BigDecimal.ZERO) > 0) { //If 
            BigDecimal discountFactor = BigDecimal.valueOf(100).subtract(loyaltyDiscount)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalAmount = totalAmount.multiply(discountFactor);
        }

//        System.out.println("Total Amount After Discount: " + totalAmount);
    }

    // Apply loyalty discount
    public void applyDiscount(BigDecimal discount) {
        this.loyaltyDiscount = discount;
        calculateTotal();
    }

    // Getters
    public UUID getSaleId() {
        return saleId;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public BigDecimal getLoyaltyDiscount() {
        return loyaltyDiscount;
    }
}
