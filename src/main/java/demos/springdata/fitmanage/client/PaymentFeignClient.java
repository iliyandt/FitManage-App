package demos.springdata.fitmanage.client;

import demos.springdata.fitmanage.domain.dto.payment.AccountLinkResponse;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;
import demos.springdata.fitmanage.domain.dto.payment.ConnectedCheckoutRequest;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service", url = "${payment-service.url}")
public interface PaymentFeignClient {
    @PostMapping("/api/v1/payments/saas/checkout")
    String createSaasCheckoutSession(@RequestBody CheckoutRequest request);

    @PostMapping("/api/v1/payments/connect/checkout")
    String createMemberCheckoutSession(@RequestParam("connectedAccountId") String connectedAccountId, @RequestBody ConnectedCheckoutRequest request);

    @PostMapping("/api/v1/payments/connect/onboard")
    AccountLinkResponse createAccountLink(
            @RequestParam("connectedAccountId") String connectedAccountId,
            @RequestParam("returnUrl") String returnUrl,
            @RequestParam("refreshUrl") String refreshUrl
    );

    @PostMapping("/api/v1/payments/connect/create-account")
    String createConnectedAccount(@RequestBody TenantDto tenantDto);
}
