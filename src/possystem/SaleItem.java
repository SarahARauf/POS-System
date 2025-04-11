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

/*
SaleItem model

Calculates total price by multiplying the unit price by the quantity added by the user
Will only be created if user adds an item to cart
*/

public class SaleItem {
    private Product product;
    private int quantity;
    private BigDecimal unitPrice;

    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    // Calculate line total
    public BigDecimal calculateLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setQuantity(int quantity){
        this.quantity = quantity;
//        calculateLineTotal();
    }
}
