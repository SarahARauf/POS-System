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

public interface PaymentGateway {
    TransactionResult processPayment(BigDecimal amount, PaymentDetails paymentDetails);
}