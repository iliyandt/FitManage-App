package demos.springdata.fitmanage.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.service.StripeService;
import demos.springdata.fitmanage.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeServiceImpl.class);
    private final TenantService tenantService;
    private final ObjectMapper objectMapper;

    @Autowired
    public StripeServiceImpl(TenantService tenantService, ObjectMapper objectMapper) {
        this.tenantService = tenantService;
        this.objectMapper = objectMapper;
    }

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String endpointSecret;

    @Value("${stripe.api.key}")
    private String apiKey;




    @Override
    public Session createCheckoutSession(CheckoutRequest checkoutRequest) throws StripeException {

        Stripe.apiKey = apiKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://dam-il.netlify.app/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("https://dam-il.netlify.app/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(checkoutRequest.getCurrency())
                                                .setUnitAmount(checkoutRequest.getAmount())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(checkoutRequest.getPlan() + " - " + checkoutRequest.getAbonnementDuration())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("tenantId", checkoutRequest.getTenantId())
                .putMetadata("planName", checkoutRequest.getPlan())
                .putMetadata("abonnementDuration", checkoutRequest.getAbonnementDuration())
                .build();

        return Session.create(params);
    }

    @Override
    public Session getSession(String sessionId) throws StripeException {
        Stripe.apiKey = apiKey;
        return Session.retrieve(sessionId);
    }

    @Override
    public void webhookEvent(String payload, String signatureHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            LOGGER.error("SignatureVerificationException (webhook)", e);
            throw new FitManageAppException("Failed signature verification", ApiErrorCode.CONFLICT);
        }


        Map<String, Object> props = objectMapper.convertValue(event.getData(), Map.class);
        Object dataMap = props.get("object");

        Map<String, Object> om = objectMapper.convertValue(dataMap, Map.class);
        LOGGER.info("Event received: {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed":
                try {

                    Session session = objectMapper.convertValue(event.getDataObjectDeserializer().getObject().orElse(null), Session.class);
                    if (session == null) {
                        LOGGER.error("Failed to deserialize Stripe session");
                        return;
                    }


                    Map<String, String> metadata = session.getMetadata();

                    Long tenantId = Long.valueOf(metadata.get("tenantId"));
                    String planName = metadata.get("planName");
                    String duration = metadata.get("abonnementDuration");


                    tenantService.createAbonnement(tenantId, Abonnement.valueOf(planName), duration);

                    LOGGER.info("Abonnement created for tenantId={}", tenantId);

                } catch (Exception ex) {
                    LOGGER.error("Error processing webhook event", ex);
                    throw new FitManageAppException("Error processing webhook event", ApiErrorCode.INTERNAL_ERROR);
                }
                break;
        }

    }
}

