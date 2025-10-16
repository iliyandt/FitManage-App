package demos.springdata.fitmanage.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.checkout.Session;
import demos.springdata.fitmanage.service.StripeConnectService;
import org.springframework.stereotype.Service;

@Service
public class StripeConnectServiceImpl implements StripeConnectService {

    @Override
    public Account createConnectedAccount() throws StripeException {
        return null;
    }

    @Override
    public AccountLink createAccountLink(String connectedAccountId) throws StripeException {
        return null;
    }

    @Override
    public Session createCheckoutSession(String connectedAccountId) throws StripeException {
        return null;
    }
}
