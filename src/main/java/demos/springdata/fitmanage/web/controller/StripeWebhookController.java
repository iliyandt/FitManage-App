package demos.springdata.fitmanage.web.controller;


import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Scanner;

@RestController
@RequestMapping("api/v1/stripe/webhook")
public class StripeWebhookController {

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String endpointSecret;

    @Value("${stripe.api.key}")
    private String apiKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeWebhookController.class);
    private final TenantService tenantService;

    public StripeWebhookController(TenantService tenantService) {
        this.tenantService = tenantService;
    }


    @PostMapping
    public ResponseEntity<String> handleStripeEvent(HttpServletRequest request) throws StripeException {
        Stripe.apiKey = apiKey;
        String sigHeader = request.getHeader("Stripe-Signature");

        String payload;
        try (Scanner scanner = new Scanner(request.getInputStream(), "UTF-8")) {
            payload = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to read payload");
        }


        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);


            if (session != null) {
                session = Session.retrieve(session.getId());
                LOGGER.info("session is not null");
                String tenantId = session.getMetadata().get("tenantId");
                Abonnement planName = Abonnement.valueOf(session.getMetadata().get("planName"));
                String abonnementDuration = session.getMetadata().get("abonnementDuration");

                tenantService.createAbonnement(Long.valueOf(tenantId), planName, abonnementDuration);
            }
        }

        return ResponseEntity.ok("Success");
    }
}
