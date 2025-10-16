package demos.springdata.fitmanage.domain.dto.tenant;

public class TenantNonAuthInfoDto {
    private Long tenantId;
    private String name;
    private String city;
    private String address;

    public TenantNonAuthInfoDto() {
    }

    public Long getTenantId() {
        return tenantId;
    }

    public TenantNonAuthInfoDto setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getName() {
        return name;
    }

    public TenantNonAuthInfoDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getCity() {
        return city;
    }

    public TenantNonAuthInfoDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public TenantNonAuthInfoDto setAddress(String address) {
        this.address = address;
        return this;
    }
}
