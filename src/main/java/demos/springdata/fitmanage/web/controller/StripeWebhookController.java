package demos.springdata.fitmanage.web.controller;
import demos.springdata.fitmanage.service.StripeConnectService;
import demos.springdata.fitmanage.service.StripeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/webhooks")
public class StripeWebhookController {

    private final StripeConnectService stripeConnectService;

    public StripeWebhookController(StripeConnectService stripeConnectService) {
        this.stripeConnectService = stripeConnectService;
    }

    @PostMapping
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader HttpHeaders headers) {

        String signatureHeader = headers.getFirst("Stripe-Signature");
        stripeConnectService.webhookEventConnected(payload, signatureHeader);
        return ResponseEntity.ok("Success");
    }


    @PostMapping("/connectedAccount")
    public ResponseEntity<String> webhookConnectedAccounts(
            @RequestBody String payload,
            @RequestHeader HttpHeaders headers) {

        String signatureHeader = headers.getFirst("Stripe-Signature");
        stripeConnectService.webhookEventConnected(payload, signatureHeader);
        return ResponseEntity.ok("Success");
    }

}

