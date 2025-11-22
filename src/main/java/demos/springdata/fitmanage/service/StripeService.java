package demos.springdata.fitmanage.service;
import demos.springdata.fitmanage.domain.dto.payment.AccountLinkResponse;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;
import demos.springdata.fitmanage.domain.dto.payment.ConnectedCheckoutRequest;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;

public interface StripeService {
    String createSaasCheckoutSession(CheckoutRequest checkoutRequest);
    String createMemberCheckoutSession(String connectedAccountId, ConnectedCheckoutRequest request);
    AccountLinkResponse createAccountLink(String connectedAccountId, String returnUrl, String refreshUrl);
    void createConnectedAccount(TenantDto tenantDto);
}
