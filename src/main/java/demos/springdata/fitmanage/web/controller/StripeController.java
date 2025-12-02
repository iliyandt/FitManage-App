package demos.springdata.fitmanage.web.controller;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.payment.*;
import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.service.StripeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stripe")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MEMBER')")
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }


    @PostMapping("/create-checkout-session")
    public ResponseEntity<String> createSaasCheckoutSession(@RequestBody CheckoutRequest request) {
        String session = stripeService.createSaasCheckoutSession(request);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/create-checkout-session/connectedAccounts")
    public ResponseEntity<ApiResponse<String>> createMemberCheckoutSession(@RequestParam String connectedAccountId, @RequestBody ConnectedCheckoutRequest request) {
        String session = stripeService.createMemberCheckoutSession(connectedAccountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(session));
    }

    @PostMapping("/account_link")
    public ResponseEntity<ApiResponse<AccountLinkResponse>> getAccountLink(@RequestBody AccountLinkRequest request) {
        AccountLinkResponse response = stripeService.createAccountLink(request.getConnectedAccountId(), request.getReturnUrl(), request.getRefreshUrl());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/connected-account")
    public ResponseEntity<ApiResponse<String>> createConnectedAccount(@RequestBody TenantDto tenant) {
        stripeService.createConnectedAccount(tenant);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Account created"));
    }
}
