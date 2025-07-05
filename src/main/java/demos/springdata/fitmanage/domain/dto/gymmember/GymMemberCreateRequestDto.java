package demos.springdata.fitmanage.domain.dto.gymmember;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GymMemberCreateRequestDto {

    @NotBlank
    @Size(min = 2, max = 15)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;

    @NotBlank
    @Column(unique = true)
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank
    @Column(unique = true)
    private String phone;

    public GymMemberCreateRequestDto() {
    }

    public GymMemberCreateRequestDto(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
