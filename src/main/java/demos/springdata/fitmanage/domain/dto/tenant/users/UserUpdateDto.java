package demos.springdata.fitmanage.domain.dto.tenant.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateDto {

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    public UserUpdateDto() {}

    public UserUpdateDto(String phone, String address, String city) {
        this.phone = phone;
        this.address = address;
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
