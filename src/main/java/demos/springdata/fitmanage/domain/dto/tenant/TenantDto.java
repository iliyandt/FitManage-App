package demos.springdata.fitmanage.domain.dto.tenant;

import java.time.LocalDate;

public class TenantDto {
    private String name;
    private String email;
    private String address;
    private String city;
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

    public String getEmail() {
        return email;
    }

    public TenantDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public TenantDto setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public TenantDto setCity(String city) {
        this.city = city;
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
