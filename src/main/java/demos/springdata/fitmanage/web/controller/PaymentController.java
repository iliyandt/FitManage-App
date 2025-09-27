package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.payment.CheckoutResponse;
import demos.springdata.fitmanage.domain.dto.payment.TerminalPaymentResponse;
import demos.springdata.fitmanage.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payments")
@PreAuthorize("hasAuthority('FACILITY_ADMIN')")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> createPayment(@RequestParam BigDecimal amount) {
        CheckoutResponse redirectUrl = paymentService.startHostedCheckout(amount.multiply(BigDecimal.valueOf(100)).longValue(), "EUR");
        return ResponseEntity.ok(redirectUrl);
    }

    @GetMapping("/status/{hostedCheckoutId}")
    public ResponseEntity<String> getPaymentStatus(@PathVariable String hostedCheckoutId) {

        String status = paymentService.checkPaymentStatus(hostedCheckoutId);
        return ResponseEntity.ok(status);
    }

//    @PostMapping("/terminal-payment")
//    public ResponseEntity<TerminalPaymentResponse> startTerminalPayment(@RequestParam Long amount) {
//        TerminalPaymentResponse response = paymentService.startTerminalPayment(amount, "EUR");
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/terminal-status/{transactionId}")
//    public ResponseEntity<String> getTerminalPaymentStatus(@PathVariable String transactionId) {
//        String status = paymentService.checkTerminalPaymentStatus(transactionId);
//        return ResponseEntity.ok(status);
//    }
}
