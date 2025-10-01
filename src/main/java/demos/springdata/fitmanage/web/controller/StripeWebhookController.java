package demos.springdata.fitmanage.web.controller;


import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stripe/webhook")
public class StripeWebhookController {

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String endpointSecret;

    private final TenantService tenantService;

    public StripeWebhookController(TenantService tenantService) {
        this.tenantService = tenantService;
    }


    @PostMapping
    public ResponseEntity<String> handleStripeEvent(HttpServletRequest request, @RequestBody String payload) {

        String sigHeader = request.getHeader("Stripe-Signature");

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session != null) {

                String tenantId = session.getMetadata().get("tenantId");
                Abonnement planName = Abonnement.valueOf(session.getMetadata().get("planName"));
                String abonnementDuration = session.getMetadata().get("abonnementDuration");


                //todo: create tenant subscription
                tenantService.createAbonnement(Long.valueOf(tenantId), planName, abonnementDuration);
            }
        }

        return ResponseEntity.ok("Success");
    }
}
