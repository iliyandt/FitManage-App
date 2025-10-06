package demos.springdata.fitmanage.web.controller;


import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("api/v1/stripe/webhook")
public class StripeWebhookController {

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String endpointSecret;

    @Value("${STRIPE_API_KEY}")
    private String apiKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeWebhookController.class);
    private final TenantService tenantService;

    public StripeWebhookController(TenantService tenantService) {
        this.tenantService = tenantService;
    }


    @PostMapping
    public ResponseEntity<String> handleStripeEvent(HttpServletRequest request) throws StripeException, IOException {
        Stripe.apiKey = apiKey;


        byte[] payloadBytes = request.getInputStream().readAllBytes();
        String sigHeader = request.getHeader("Stripe-Signature");

        String payload = new String(payloadBytes, StandardCharsets.UTF_8);
        LOGGER.info("Payload: {}", payload);
        LOGGER.info("Stripe-Signature: {}", sigHeader);

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        LOGGER.info("Event: {}", event);

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);

            if (session == null) {
                LOGGER.warn("Unable to deserialize Stripe object");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
            }

            if (session != null) {
                session = Session.retrieve(session.getId());

                String tenantId = session.getMetadata().get("tenantId");
                Abonnement planName = Abonnement.valueOf(session.getMetadata().get("planName"));
                String abonnementDuration = session.getMetadata().get("abonnementDuration");

                tenantService.createAbonnement(Long.valueOf(tenantId), planName, abonnementDuration);
            }
        }

        return ResponseEntity.ok("Success");
    }


//    @PostMapping
//    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request, @RequestBody String payload) {
//        Stripe.apiKey = apiKey;
//
//        String sigHeader = request.getHeader("Stripe-Signature");
//        Event event;
//
//        LOGGER.info("Stripe-Signature header: {}", sigHeader);
//        LOGGER.info("Payload: {}", payload);
//
//        try {
//            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
//        } catch (SignatureVerificationException e) {
//            LOGGER.error("Invalid Stripe signature", e);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
//        }
//
//
//        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
//        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);
//
//        if (stripeObject == null) {
//            LOGGER.warn("Unable to deserialize Stripe object");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
//        }
//
//        switch (event.getType()) {
//            case "checkout.session.completed":
//                handleCheckoutSessionCompleted((Session) stripeObject);
//                break;
//
//            case "payment_intent.succeeded":
//                LOGGER.info("PaymentIntent succeeded");
//                break;
//
//            default:
//                LOGGER.info("Unhandled event type: {}", event.getType());
//                break;
//        }
//
//        return ResponseEntity.ok("Success");
//    }
//
//
//    private void handleCheckoutSessionCompleted(Session session) {
//        LOGGER.info("Checkout session completed: {}", session.getId());
//
//        try {
//            Session fullSession = Session.retrieve(session.getId());
//
//            Map<String, String> metadata = fullSession.getMetadata();
//            String tenantId = metadata.get("tenantId");
//            String planName = metadata.get("planName");
//            String duration = metadata.get("abonnementDuration");
//
//            LOGGER.info("Tenant ID: {}, Plan: {}, Duration: {}", tenantId, planName, duration);
//            tenantService.createAbonnement(Long.valueOf(tenantId), Abonnement.valueOf(planName), duration);
//
//        } catch (StripeException e) {
//            LOGGER.error("Failed to retrieve full session from Stripe", e);
//        }
//    }

}

