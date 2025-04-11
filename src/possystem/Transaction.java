/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package possystem;

/**
 *
 * @author Sarah
 */
import java.util.UUID;
import java.math.BigDecimal;

public class Transaction {
    private UUID transactionId;
    private BigDecimal amount;
    private String type;

    public Transaction(UUID transactionId, BigDecimal amount, String type) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
    }

    // Getters
    public UUID getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}