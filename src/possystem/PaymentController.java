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
        
public class PaymentController {
    private PaymentGateway paymentGateway;

    public PaymentController(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public TransactionResult processTransaction(BigDecimal amount, PaymentDetails paymentDetails) {
        return paymentGateway.processPayment(amount, paymentDetails);
    }
}