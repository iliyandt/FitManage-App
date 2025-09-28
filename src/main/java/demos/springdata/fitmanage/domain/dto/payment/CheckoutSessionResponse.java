package demos.springdata.fitmanage.domain.dto.payment;

public class CheckoutSessionResponse {
    private String id;
    private String url;
    private String status;

    public CheckoutSessionResponse(String id, String url, String status) {
        this.id = id;
        this.url = url;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public CheckoutSessionResponse setId(String id) {
        this.id = id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public CheckoutSessionResponse setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public CheckoutSessionResponse setStatus(String status) {
        this.status = status;
        return this;
    }
}
