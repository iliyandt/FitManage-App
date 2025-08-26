package demos.springdata.fitmanage.domain.dto.tenant;

import java.time.LocalDate;

public class TenantDto {
    private Long id;
    private String name;
    private String businessEmail;
    private String address;
    private String city;
    private LocalDate subscriptionValidUntil;

    public TenantDto() {
    }

    public Long getId() {
        return id;
    }

    public TenantDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TenantDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public TenantDto setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
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
