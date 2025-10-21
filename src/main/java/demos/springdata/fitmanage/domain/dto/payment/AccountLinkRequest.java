package demos.springdata.fitmanage.domain.dto.payment;

public class AccountLinkRequest {
    private String connectedAccountId;
    private String returnUrl;
    private String refreshUrl;

    public AccountLinkRequest() {
    }

    public String getConnectedAccountId() {
        return connectedAccountId;
    }

    public AccountLinkRequest setConnectedAccountId(String connectedAccountId) {
        this.connectedAccountId = connectedAccountId;
        return this;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public AccountLinkRequest setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
        return this;
    }

    public String getRefreshUrl() {
        return refreshUrl;
    }

    public AccountLinkRequest setRefreshUrl(String refreshUrl) {
        this.refreshUrl = refreshUrl;
        return this;
    }
}
