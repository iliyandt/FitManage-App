package demos.springdata.fitmanage.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import demos.springdata.fitmanage.domain.dto.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
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

import java.util.Map;
import java.util.Optional;


@Service
public class StripeServiceImpl implements StripeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripeServiceImpl.class);
    private final TenantService tenantService;
    private final MembershipService membershipService;

    @Autowired
    public StripeServiceImpl(TenantService tenantService, MembershipService membershipService) {
        this.tenantService = tenantService;
        this.membershipService = membershipService;
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
                .putMetadata("abonnementDuration", checkoutRequest.getAbonnementDuration());

        if (checkoutRequest.getMemberId() != null) {
            params.putMetadata("memberId", String.valueOf(checkoutRequest.getMemberId()));
            params.putMetadata("employment", String.valueOf(checkoutRequest.getEmployment()));
        }

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
            event = Webhook.constructEvent(payload, signatureHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new FitManageAppException("Failed signature verification", ApiErrorCode.CONFLICT);
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Optional<StripeObject> stripeObjectOptional = event.getDataObjectDeserializer().getObject();
            if (stripeObjectOptional.isEmpty()) {
                LOGGER.error("No StripeObject present in event data object deserializer");
                return;
            }

            StripeObject stripeObject = stripeObjectOptional.get();

            if (!(stripeObject instanceof Session session)) {
                LOGGER.error("Webhook did not contain a Session object.");
                return;
            }

            try {
                Map<String, String> metadata = session.getMetadata();
                String memberIdMetadata = metadata.get("memberId");
                Long tenantId = Long.valueOf(metadata.get("tenantId"));
                String planName = metadata.get("planName");
                String duration = metadata.get("abonnementDuration");

                if (memberIdMetadata != null) {
                    Long memberId = Long.valueOf(memberIdMetadata);
                    Employment employment = Employment.valueOf(metadata.get("employment"));
                    MemberSubscriptionRequestDto requestDto = handleMemberStripeSubscription(planName, duration, employment);
                    membershipService.setupMembershipPlan(memberId, requestDto);
                    LOGGER.info("Abonnement created for memberId={} in tenant with id={}", memberId, tenantId);
                } else {
                    tenantService.createAbonnement(tenantId, Abonnement.valueOf(planName), duration);
                    LOGGER.info("Abonnement created for tenantId={}", tenantId);
                }

            } catch (Exception ex) {
                LOGGER.error("Error processing webhook event", ex);
                LOGGER.error("Payload causing error: {}", payload);
                throw new FitManageAppException("Error processing webhook event", ApiErrorCode.INTERNAL_ERROR);
            }

        } else if ("customer.subscription.updated".equals(event.getType())){
            //TODO:
        } else {
            LOGGER.warn("Unhandled event type: {}", event.getType());
        }
    }

    private MemberSubscriptionRequestDto handleMemberStripeSubscription(String subscriptionPlan, String allowedVisits, Employment employment) {
        MemberSubscriptionRequestDto dto = new MemberSubscriptionRequestDto();

        SubscriptionPlan plan = SubscriptionPlan.valueOf(subscriptionPlan.toUpperCase());
        dto.setSubscriptionPlan(plan);
        dto.setEmployment(employment);

        if (plan.isVisitBased()) {
            dto.setAllowedVisits(Integer.valueOf(allowedVisits));
        }

        return dto;
    }

}

