package demos.springdata.fitmanage.service.impl;

import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.service.MembershipService;
import demos.springdata.fitmanage.service.StripeService;
import demos.springdata.fitmanage.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeServiceImpl.class);
    private final TenantService tenantService;


    @Autowired
    public StripeServiceImpl(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String endpointSecret;

    @Value("${stripe.api.key}")
    private String apiKey;


    @Override
    public Session createCheckoutSession(CheckoutRequest checkoutRequest) throws StripeException {

        Stripe.apiKey = apiKey;

        SessionCreateParams.Builder params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)//recurring??
                .setSuccessUrl("https://damilsoft.com/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("https://damilsoft.com/cancel")
                .setCustomerEmail(checkoutRequest.getBusinessEmail())
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
                .putMetadata("businessEmail", checkoutRequest.getBusinessEmail());

        return Session.create(params.build());
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
          event = ApiResource.GSON.fromJson(payload, Event.class);
        } catch (JsonSyntaxException ex) {
            LOGGER.warn("Invalid payload: {}", payload);
            throw new FitManageAppException("Invalid payload", ApiErrorCode.BAD_REQUEST);
        }

        if (signatureHeader == null || endpointSecret == null) {
            LOGGER.warn("Webhook signature or secret is missing.");
            throw new FitManageAppException("Missing webhook signature/secret", ApiErrorCode.BAD_REQUEST);
        }

        try {
            event = Webhook.constructEvent(
                    payload, signatureHeader, endpointSecret
            );

        } catch (SignatureVerificationException ex) {
            LOGGER.warn("Webhook error while validating signature.", ex);
            throw new FitManageAppException("Invalid signature", ApiErrorCode.BAD_REQUEST);
        }


        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            LOGGER.error("Failed to deserialize event data object. API version mismatch? Event ID: {}", event.getId());
            throw new FitManageAppException("Event deserialization failed", ApiErrorCode.INTERNAL_ERROR);
        }

        LOGGER.info("Handling Stripe event: {} ({})", event.getType(), event.getId());
        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) stripeObject;
                LOGGER.info("Payment for {} succeeded.", session.getAmountTotal());
                tenantService.createAbonnement
                        (
                                Long.valueOf(session.getMetadata().get("tenantId")),
                                Abonnement.valueOf(session.getMetadata().get("planName")),
                                session.getMetadata().get("abonnementDuration")
                        );

                LOGGER.info("Abonnement {} created for tenant with ID: {}", session.getMetadata().get("planName"), session.getMetadata().get("tenantId"));
                break;
            //case "":
            default:
                LOGGER.warn("Unhandled event type: {}", event.getType());
        }
    }

}
