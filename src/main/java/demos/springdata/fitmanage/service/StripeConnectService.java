package demos.springdata.fitmanage.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.checkout.Session;
import demos.springdata.fitmanage.domain.dto.payment.AccountLinkResponse;

public interface StripeConnectService {
    Account createConnectedAccount(String tenantEmail) throws StripeException;
    AccountLinkResponse createAccountLink(String connectedAccountId, String returnUrl, String refreshUrl) throws StripeException;
    Session createCheckoutSession(String connectedAccountId) throws StripeException;
}
