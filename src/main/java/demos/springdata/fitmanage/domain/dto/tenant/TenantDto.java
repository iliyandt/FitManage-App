package demos.springdata.fitmanage.domain.dto.tenant;

import java.time.LocalDate;

public class TenantDto {
    private String name;
    private LocalDate subscriptionValidUntil;

    public TenantDto() {
    }

    public String getName() {
        return name;
    }

    public TenantDto setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDate getSubscriptionValidUntil() {
        return subscriptionValidUntil;
    }

    public TenantDto setSubscriptionValidUntil(LocalDate subscriptionValidUntil) {
        this.subscriptionValidUntil = subscriptionValidUntil;
        return this;
    }
}
