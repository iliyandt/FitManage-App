package demos.springdata.fitmanage.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import demos.springdata.fitmanage.domain.dto.payment.AccountLinkResponse;
import demos.springdata.fitmanage.service.StripeConnectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StripeConnectServiceImpl implements StripeConnectService {


    private static final Logger LOGGER = LoggerFactory.getLogger(StripeConnectServiceImpl.class);


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
                        .setBusinessType(AccountCreateParams.BusinessType.COMPANY)
                        .setBusinessProfile(
                                AccountCreateParams.BusinessProfile
                                        .builder()
                                        .setName("Set current tenant name") //TODO: set current tenant name
                                        .setProductDescription("Subscription")
                                        .setMcc("7941") //TODO: check if the mcc is correct
                                        .build()
                        )
                        .build();

        return Account.create(params);
    }


    @Override
    public AccountLinkResponse createAccountLink(String connectedAccountId, String returnUrl, String refreshUrl) throws StripeException {
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
    public Session createCheckoutSession(String connectedAccountId) throws StripeException {
        return null;
    }
}
