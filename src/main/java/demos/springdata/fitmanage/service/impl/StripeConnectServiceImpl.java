package demos.springdata.fitmanage.service.impl;

import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import demos.springdata.fitmanage.domain.dto.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.payment.AccountLinkResponse;
import demos.springdata.fitmanage.domain.dto.payment.ConnectedCheckoutRequest;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.service.MembershipService;
import demos.springdata.fitmanage.service.StripeConnectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeConnectServiceImpl implements StripeConnectService {

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String endpointSecret;

    @Value("${stripe.api.key}")
    private String apiKey;

    private final MembershipService membershipService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StripeConnectServiceImpl.class);

    public StripeConnectServiceImpl(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @Override
    public Account createConnectedAccount(Tenant tenant) throws StripeException {
        AccountCreateParams.Capabilities capabilities =
                AccountCreateParams.Capabilities.builder()
                        .setCardPayments(
                                AccountCreateParams.Capabilities.CardPayments.builder().setRequested(true).build()
                        )
                        .setTransfers(
                                AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build()
                        )
                        .build();


        AccountCreateParams params =
                AccountCreateParams.builder()
                        .setType(AccountCreateParams.Type.EXPRESS)
                        .setCountry("BG")
                        .setEmail(tenant.getBusinessEmail())
                        .setCapabilities(capabilities)
                        .setBusinessType(AccountCreateParams.BusinessType.COMPANY)
                        .setBusinessProfile(
                                AccountCreateParams.BusinessProfile
                                        .builder()
                                        .setName(tenant.getName())
                                        .setProductDescription("Subscription")
                                        .setMcc("7941")
                                        .build()
                        )
                        .build();

        return Account.create(params);
    }


    @Override
    public AccountLinkResponse createAccountLink(String connectedAccountId, String returnUrl, String refreshUrl) throws StripeException {

        Stripe.apiKey = apiKey;

        AccountLinkCreateParams params =
                AccountLinkCreateParams.builder()
                        .setAccount(connectedAccountId)
                        .setRefreshUrl(refreshUrl)
                        .setReturnUrl(returnUrl)
                        .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                        .setCollect(AccountLinkCreateParams.Collect.EVENTUALLY_DUE)
                        .build();

        Account account = Account.retrieve(connectedAccountId);

        LOGGER.info("Account email: {}", account.getEmail());

        AccountLink accountLink = AccountLink.create(params);

        LOGGER.info("Account link info: {}", accountLink);

        return new AccountLinkResponse()
                .setUrl(accountLink.getUrl())
                .setCreated(accountLink.getCreated())
                .setExpiresAt(accountLink.getExpiresAt());
    }

    @Override
    public Session createCheckoutSessionConnectedAccounts(String connectedAccountId, ConnectedCheckoutRequest request) throws StripeException {

        Stripe.apiKey = apiKey;

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setSuccessUrl("https://damilsoft.com/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl("https://damilsoft.com/cancel")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency(request.getCurrency())
                                                        .setUnitAmount(request.getAmount())
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName(request.getSubscriptionPlan() + " - " + request.getEmployment())
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .putMetadata("userId", request.getUserId().toString())
                        .putMetadata("subscriptionPlan", request.getSubscriptionPlan())
                        .putMetadata("employment", request.getEmployment().toString())
                        .build();


        RequestOptions options = RequestOptions.builder()
                .setStripeAccount(connectedAccountId)
                .build();

        return Session.create(params, options);
    }

    @Override
    public void webhookEventConnected(String payload, String signatureHeader) {
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

                MemberSubscriptionRequestDto requestDto = new MemberSubscriptionRequestDto()
                        .setSubscriptionPlan(SubscriptionPlan.valueOf(session.getMetadata().get("subscriptionPlan")))
                        .setEmployment(Employment.valueOf(session.getMetadata().get("employment")))
                        .setAllowedVisits(Integer.valueOf(session.getMetadata().get("allowedVisits")));


                membershipService.setupMembershipPlan(Long.valueOf(session.getMetadata().get("userId")), requestDto);

                LOGGER.info("Abonnement {} created for tenant with ID: {}", session.getMetadata().get("planName"), session.getMetadata().get("tenantId"));
                break;
            //case "":
            default:
                LOGGER.warn("Unhandled event type: {}", event.getType());
        }
    }

    @Override
    public void deleteStripeConnectedAccount(String connectedAccountId) throws StripeException {
        Stripe.apiKey = apiKey;
        Account resource = Account.retrieve(connectedAccountId);
        resource.delete();
    }
}
