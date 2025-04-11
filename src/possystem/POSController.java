/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package possystem;

/**
 *
 * @author Sarah
 */
import javax.swing.*;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;
import javax.swing.JOptionPane;

public class POSController {

    private Sale currentSale;
    private LoyaltyMember loyaltyMember;
    private ProductDAO productDAO;
    private LoyaltyMemberDAO loyaltyMemberDAO;
    private SaleDAO saleDAO;
    private SelfServicePOSUI ui;
    private PaymentController paymentController;
    
    /*
    POS controller interacts with UI and Data Objects (The database)
    */

    public POSController(Connection posDbConnection, Connection bankingDbConnection, SelfServicePOSUI ui) {
        this.productDAO = new ProductDAO(posDbConnection); // posDB Connection to Product Data Object
        this.loyaltyMemberDAO = new LoyaltyMemberDAO(posDbConnection); // posDB Connection to LoyaltyMember Data Object
        this.saleDAO = new SaleDAO(posDbConnection); // posDB Connection to Sale Data Object
        this.ui = ui; // Self Service UI

        // Initialize the MySQLBankingSystem and PaymentController
        BankingSystem bankingSystem = new BankingSystem(bankingDbConnection);
        this.paymentController = new PaymentController(bankingSystem);
    }

    // Start a new sale
    public void startNewSale() {
        this.currentSale = new Sale();
        this.loyaltyMember = null;
        System.out.println("New sale started.");
    }

    // Add item to the sale
    public void addItem(UUID productId, int quantity) {
        Product product = productDAO.getProduct(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock.");
        }

        // if the product is already in the cart, increase the quantity only
        // Collects the UUID of the products in the cart
        List<UUID> productInSale = currentSale.getItems().stream()
                .map(SaleItem::getProduct)
                .map(Product::getProductId)
                .collect(Collectors.toList());
        
        System.out.println(productInSale);

        // If the product UUID to add to the cart is already in the cart
        if (productInSale.contains(product.getProductId())) {
//            System.out.println("yes");
            // Find the product in the sales
            for (SaleItem cartItems : currentSale.getItems()) {
//                System.out.println("in the cart:"+cartItems.getProduct().getProductId());

                if (cartItems.getProduct().getProductId().equals(product.getProductId())) {

                    int quantityToAdd = cartItems.getQuantity() + quantity;
                    cartItems.setQuantity(quantityToAdd);
//                    System.out.println(cartItems.getProduct().getProductId());
//                    System.out.println(cartItems.getQuantity());
                }
            }
        } else {
            SaleItem item = new SaleItem(product, quantity);
            currentSale.addItem(item);
        }

//        SaleItem item = new SaleItem(product, quantity);
//        currentSale.addItem(item);

        ui.showCart(currentSale);

    }

    // Unfinished: Remove a product from cart (remove from Sale object)
    public void removeItem(UUID productId, int quantity) {
        
        if (productDAO.getProduct(productId) == null) {
            throw new IllegalArgumentException("Product not found.");
        }
        
        
        //check if product is in cart
      
        List<UUID> productInSale = currentSale.getItems().stream()
                .map(SaleItem::getProduct)
                .map(Product::getProductId)
                .collect(Collectors.toList());
        
        if (productInSale.contains(productId)) {
            for (SaleItem cartItems : currentSale.getItems()){
                if (cartItems.getProduct().getProductId().equals(productId)){
                    if (cartItems.getQuantity() < quantity){
                        throw new IllegalArgumentException("Quantity to remove is higher than quantity in cart");
                    }else{
                        int quantityToSubtract = cartItems.getQuantity() - quantity;
                        if (quantityToSubtract == 0){
                            currentSale.removeItem(cartItems);
                        }else{
                            cartItems.setQuantity(quantityToSubtract);
                        }
                    }

                }
            }
        }else{
            throw new IllegalArgumentException("Product not in cart");
        }



        ui.showCart(currentSale);

    }

    // Apply loyalty discount
    public void applyLoyaltyDiscount(UUID memberId) {
        this.loyaltyMember = loyaltyMemberDAO.getMember(memberId);
        if (loyaltyMember != null) {
            currentSale.applyDiscount(loyaltyMember.applyMemberDiscount());
        }
    }

    // Process payment
    public void processPayment() {
        // Prompt for payment details
        PaymentDetails paymentDetails = ui.promptPayment();
        if (paymentDetails == null) {
            JOptionPane.showMessageDialog(ui.getFrame(), "Payment canceled.", "Payment", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Process payment through the MySQL banking system
        TransactionResult result = paymentController.processTransaction(currentSale.getTotalAmount(), paymentDetails);

        if (result.isSuccess()) {
            // Update stock and save sale
            for (SaleItem item : currentSale.getItems()) {
                productDAO.updateStock(item.getProduct().getProductId(), -item.getQuantity());
            }
            saleDAO.saveSale(currentSale);

            // Show receipt
            String customerName = (loyaltyMember != null) ? loyaltyMember.getName() : "Guest";
            ui.showReceipt(currentSale, customerName);

            // Clear the current sale
            currentSale = null;
            loyaltyMember = null;
        } else {
            JOptionPane.showMessageDialog(ui.getFrame(), "Payment failed: " + result.getMessage(), "Payment Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Display available products
    public void displayProducts() {
        List<Product> products = productDAO.getAllProducts();
        ui.displayProducts(products);
    }
}
