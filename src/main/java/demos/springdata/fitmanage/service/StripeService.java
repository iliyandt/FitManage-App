package demos.springdata.fitmanage.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {
    void init();
    PaymentIntent createPaymentIntent(Long amount, String currency) throws StripeException;
}
