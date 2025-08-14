package demos.springdata.fitmanage.domain.dto.payment;

public class CheckoutResponse {
    private String redirectUrl;
    private String hostedCheckoutId;

    public CheckoutResponse(String redirectUrl, String hostedCheckoutId) {
        this.redirectUrl = redirectUrl;
        this.hostedCheckoutId = hostedCheckoutId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public CheckoutResponse setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public String getHostedCheckoutId() {
        return hostedCheckoutId;
    }

    public CheckoutResponse setHostedCheckoutId(String hostedCheckoutId) {
        this.hostedCheckoutId = hostedCheckoutId;
        return this;
    }
}
