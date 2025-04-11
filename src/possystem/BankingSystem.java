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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BankingSystem implements PaymentGateway {

    private Connection dbConnection;

    public BankingSystem(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public TransactionResult processPayment(BigDecimal amount, PaymentDetails paymentDetails) {
        // Validate payment details
        if (!isValidCard(paymentDetails)) {
            return new TransactionResult(false, "Invalid card details.");
        }

        // Check if the card exists and is valid
        String query = "SELECT a.account_id, a.balance FROM debit_card d "
                + "JOIN account a ON d.account_id = a.account_id "
                + "WHERE d.card_number = ? AND d.expiry_date = ? AND d.cvv = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, paymentDetails.getCardNumber());
            stmt.setString(2, paymentDetails.getExpiryDate());
            stmt.setString(3, paymentDetails.getCvv());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String accountId = rs.getString("account_id");
                BigDecimal balance = rs.getBigDecimal("balance");

                // Check if the account has sufficient balance
                if (balance.compareTo(amount) >= 0) {
                    // Debit the account
                    debitAccount(accountId, amount);
                    return new TransactionResult(true, "Payment successful.");
                } else {
                    return new TransactionResult(false, "Insufficient funds.");
                }
            } else {
                return new TransactionResult(false, "Card not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new TransactionResult(false, "Database error: " + e.getMessage());
        }
    }

    // Validate card details (e.g., expiry date)
    private boolean isValidCard(PaymentDetails paymentDetails) {
        String expiryDate = paymentDetails.getExpiryDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

        try {
            // Parse the expiry date (e.g., "12/25" -> December 2025)
            LocalDate cardExpiry = YearMonth.parse(expiryDate, formatter).atEndOfMonth();
            LocalDate currentDate = LocalDate.now();

            // Check if the card is not expired
            return !cardExpiry.isBefore(currentDate);
        } catch (DateTimeParseException e) {
            // Handle invalid date format
            return false;
        }
    }

    // Debit the account and record the transaction
    private void debitAccount(String accountId, BigDecimal amount) {
        String updateBalanceQuery = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
        String insertTransactionQuery = "INSERT INTO transaction (transaction_id, account_id, amount, type, timestamp) "
                + "VALUES (UUID(), ?, ?, 'DEBIT', NOW())";

        try (PreparedStatement updateStmt = dbConnection.prepareStatement(updateBalanceQuery); PreparedStatement insertStmt = dbConnection.prepareStatement(insertTransactionQuery)) {

            // Update account balance
            updateStmt.setBigDecimal(1, amount);
            updateStmt.setString(2, accountId);
            updateStmt.executeUpdate();

            // Record the transaction
            insertStmt.setString(1, accountId);
            insertStmt.setBigDecimal(2, amount);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
