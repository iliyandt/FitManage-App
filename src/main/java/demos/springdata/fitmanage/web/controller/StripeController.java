package demos.springdata.fitmanage.web.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutSessionResponse;
import demos.springdata.fitmanage.service.StripeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stripe")
@PreAuthorize("hasAuthority('FACILITY_ADMIN')")
public class StripeController {

    private final StripeService stripeService;


    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-checkout-session")
    public CheckoutSessionResponse createCheckoutSession(@RequestBody CheckoutRequest request) throws StripeException {
        Session session = stripeService.createCheckoutSession(request);
        return new CheckoutSessionResponse(session.getId(), session.getUrl(), null);
        //return Map.of("url", session.getUrl(), "id", session.getId());
    }

    @GetMapping("/session/{id}")
    public CheckoutSessionResponse getSession(@PathVariable String id) throws StripeException {
        Session session = stripeService.getSession(id);
        return new CheckoutSessionResponse(session.getId(), session.getUrl(), session.getPaymentStatus());
    }
}
