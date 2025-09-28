package demos.springdata.fitmanage.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;

import com.stripe.param.checkout.SessionCreateParams;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;
import demos.springdata.fitmanage.service.StripeService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class StripeServiceImpl implements StripeService {

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
                                                                .setName("Your Product Name")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        return Session.create(params);
    }

    @Override
    public Session getSession(String sessionId) throws StripeException {
        Stripe.apiKey = apiKey;
        return Session.retrieve(sessionId);
    }
}

