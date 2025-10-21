package demos.springdata.fitmanage.domain.dto.payment;

public class AccountLinkResponse {
    private String url;
    private Long created;
    private Long expiresAt;

    public AccountLinkResponse() {
    }

    public String getUrl() {
        return url;
    }

    public AccountLinkResponse setUrl(String url) {
        this.url = url;
        return this;
    }

    public Long getCreated() {
        return created;
    }

    public AccountLinkResponse setCreated(Long created) {
        this.created = created;
        return this;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public AccountLinkResponse setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }
}
