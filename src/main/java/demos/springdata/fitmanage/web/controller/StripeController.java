package demos.springdata.fitmanage.web.controller;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.payment.AccountLinkRequest;
import demos.springdata.fitmanage.domain.dto.payment.AccountLinkResponse;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutRequest;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutSessionResponse;
import demos.springdata.fitmanage.service.StripeConnectService;
import demos.springdata.fitmanage.service.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stripe")
@PreAuthorize("hasAuthority('FACILITY_ADMIN')")
public class StripeController {

    private final StripeService stripeService;
    private final StripeConnectService stripeConnectService;


    public StripeController(StripeService stripeService, StripeConnectService stripeConnectService) {
        this.stripeService = stripeService;
        this.stripeConnectService = stripeConnectService;
    }

    @PostMapping("/create-checkout-session")
    public CheckoutSessionResponse createCheckoutSession(@RequestBody CheckoutRequest request) throws StripeException {
        Session session = stripeService.createCheckoutSession(request);
        return new CheckoutSessionResponse(session.getId(), session.getUrl(), session.getCustomer());
    }

    @GetMapping("/session/{id}")
    public CheckoutSessionResponse getSession(@PathVariable String id) throws StripeException {
        Session session = stripeService.getSession(id);
        return new CheckoutSessionResponse(session.getId(), session.getUrl(), session.getPaymentStatus());
    }

    @PostMapping("/account_link")
    public ResponseEntity<ApiResponse<AccountLinkResponse>> getAccountLink(@RequestBody AccountLinkRequest request) throws StripeException {

        AccountLinkResponse response = stripeConnectService.createAccountLink(request.getConnectedAccountId(), request.getReturnUrl(), request.getRefreshUrl());

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @DeleteMapping("/{connectedAccountId}")
    public ResponseEntity<ApiResponse<Void>> deleteStripeConnectedAccount(@PathVariable String connectedAccountId) throws StripeException {
        stripeConnectService.deleteStripeConnectedAccount(connectedAccountId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
