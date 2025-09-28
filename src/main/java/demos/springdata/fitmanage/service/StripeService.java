package demos.springdata.fitmanage.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;

public interface StripeService {
    Session createCheckoutSession(CheckoutRequest checkoutRequest) throws StripeException;
    Session getSession(String sessionId) throws StripeException;
}
