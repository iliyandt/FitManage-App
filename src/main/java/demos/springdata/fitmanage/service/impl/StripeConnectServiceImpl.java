package demos.springdata.fitmanage.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import demos.springdata.fitmanage.service.StripeConnectService;
import org.springframework.stereotype.Service;

@Service
public class StripeConnectServiceImpl implements StripeConnectService {

    @Override
    public Account createConnectedAccount(String tenantEmail) throws StripeException {
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
                        .setEmail(tenantEmail)
                        .setCapabilities(capabilities)
                        .build();

        return Account.create(params);
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
