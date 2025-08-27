package demos.springdata.fitmanage.domain.dto.users;


import com.fasterxml.jackson.annotation.JsonFormat;
import demos.springdata.fitmanage.domain.enums.Gender;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public class UserUpdateDto {
    @Size(min = 2, max = 15)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First name must start with a capital letter and contain only letters")
    private String firstName;
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last name must start with a capital letter and contain only letters")
    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private OffsetDateTime birthDate;
    private String username;
    private String phone;
    private String address;
    private String city;
    private Gender gender;

    public UserUpdateDto() {}

    public String getFirstName() {
        return firstName;
    }

    public UserUpdateDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserUpdateDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public UserUpdateDto setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserUpdateDto setUsername(String username) {
        this.username = username;
        return this;
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

    public Gender getGender() {
        return gender;
    }

    public UserUpdateDto setGender(Gender gender) {
        this.gender = gender;
        return this;
    }
}
