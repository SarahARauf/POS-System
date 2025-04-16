/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package possystem;

/**
 *
 * @author Sarah
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

/*
This class works as the main
*/

public class POSSystem {
    public static void main(String[] args) {
        // Simulate database connections
        Connection posDbConnection = null;
        Connection bankingDbConnection = null;
        try {
            // Connect to pos_db
            posDbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "1");

            // Connect to banking_db
            bankingDbConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking_db", "root", "1");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Initialize UI
        SelfServicePOSUI ui = new SelfServicePOSUI();

        // Initialize MySQLBankingSystem
        BankingSystem bankingSystem = new BankingSystem(bankingDbConnection);

        // Initialize POSController
        POSController posController = new POSController(posDbConnection,bankingDbConnection, ui);
        
        // Setting the posController to the UI
        ui.setController(posController);

        // Display available products
        posController.displayProducts();

        // Start a new sale
        posController.startNewSale();

        //All of the lines of codes at the bottom will be deleted, this is just for testing purposes
        // Add items to the cart
        UUID productId = UUID.fromString("00112233-4455-6677-8899-aabbccddeeff"); // Example product ID
        posController.addItem(productId, 2);
        
        posController.addItem(productId, 2);
        
        posController.removeItem(productId, 3);

//        // Apply loyalty discount (if applicable)
//        UUID memberId = UUID.fromString("1a2b3c4d-5e6f-4a8b-9c0d-1e2f3a4b5c6d"); // Example loyalty member ID
//        posController.applyLoyaltyDiscount(memberId);
//
//        // Process payment
//        posController.processPayment();
    }
}