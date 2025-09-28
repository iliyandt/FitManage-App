package demos.springdata.fitmanage.web.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import demos.springdata.fitmanage.service.StripeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stripe")
@PreAuthorize("hasAuthority('FACILITY_ADMIN')")
public class StripeController {

    private final StripeService stripeService;


    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-payment-intent")
    public Map<String, String> createPaymentIntent(@RequestBody Map<String, Object> data) throws StripeException {
        Long amount = Long.parseLong(data.get("amount").toString());
        String currency = data.get("currency").toString();

        PaymentIntent paymentIntent = stripeService.createPaymentIntent(amount, currency);

        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        return response;
    }
}
