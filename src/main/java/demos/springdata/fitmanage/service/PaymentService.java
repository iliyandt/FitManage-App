package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.payment.CheckoutResponse;
import demos.springdata.fitmanage.domain.dto.payment.TerminalPaymentResponse;

public interface PaymentService {
    CheckoutResponse startHostedCheckout(Long amount, String currency);
    String checkPaymentStatus(String hostedCheckoutId);
    TerminalPaymentResponse startTerminalPayment(Long amount, String currency);
    String checkTerminalPaymentStatus(String transactionId);
}
