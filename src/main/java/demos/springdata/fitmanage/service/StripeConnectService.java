package demos.springdata.fitmanage.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.checkout.Session;

public interface StripeConnectService {
    Account createConnectedAccount() throws StripeException;
    AccountLink createAccountLink(String connectedAccountId) throws StripeException;
    Session createCheckoutSession(String connectedAccountId) throws StripeException;
}
