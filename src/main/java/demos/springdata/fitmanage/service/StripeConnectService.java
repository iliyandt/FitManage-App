package demos.springdata.fitmanage.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.checkout.Session;
import demos.springdata.fitmanage.domain.dto.payment.AccountLinkResponse;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutSessionResponse;
import demos.springdata.fitmanage.domain.dto.payment.ConnectedCheckoutRequest;
import demos.springdata.fitmanage.domain.entity.Tenant;

public interface StripeConnectService {
    Account createConnectedAccount(Tenant tenant) throws StripeException;
    AccountLinkResponse createAccountLink(String connectedAccountId, String returnUrl, String refreshUrl) throws StripeException;
    Session createCheckoutSessionConnectedAccounts(String connectedAccountId, ConnectedCheckoutRequest request) throws StripeException;
    void webhookEventConnected(String payload, String signatureHeader);
    void deleteStripeConnectedAccount(String connectedAccountId) throws StripeException;
}
