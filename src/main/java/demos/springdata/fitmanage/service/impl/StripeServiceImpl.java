package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.client.PaymentFeignClient;
import demos.springdata.fitmanage.domain.dto.payment.AccountLinkResponse;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;
import demos.springdata.fitmanage.domain.dto.payment.ConnectedCheckoutRequest;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.service.StripeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class StripeServiceImpl implements StripeService {

    private final PaymentFeignClient paymentClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(StripeServiceImpl.class);

    public StripeServiceImpl(PaymentFeignClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Override
    public String createSaasCheckoutSession(CheckoutRequest checkoutRequest) {
        return paymentClient.createSaasCheckoutSession(checkoutRequest);
    }

    @Override
    public String createMemberCheckoutSession(String connectedAccountId, ConnectedCheckoutRequest request){
        return paymentClient.createMemberCheckoutSession(connectedAccountId, request);
    }

    @Override
    public AccountLinkResponse createAccountLink(String tenantId, String returnUrl, String refreshUrl) {
        return paymentClient.createAccountLink(tenantId, returnUrl, refreshUrl);
    }

    @Override
    public void createConnectedAccount(TenantDto tenantDto) {
        paymentClient.createConnectedAccount(tenantDto);
    }

}
