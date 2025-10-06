package demos.springdata.fitmanage.web.controller;
import demos.springdata.fitmanage.service.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1")
public class StripeWebhookController {

    private final StripeService stripeService;

    public StripeWebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping(value = "/stripe/webhook")
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader HttpHeaders headers) {

        String signatureHeader = headers.getFirst("Stripe-Signature");
        stripeService.webhookEvent(payload, signatureHeader);
        return ResponseEntity.ok("Success");
    }

}

