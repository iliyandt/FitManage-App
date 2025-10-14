package demos.springdata.fitmanage.domain.dto.member.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import demos.springdata.fitmanage.domain.enums.Gender;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public class    MemberCreateRequestDto {

    @NotBlank(message = "First name must not be blank")
    @Size(min = 2, max = 15, message = "First Name must be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First name must start with a capital letter and contain only letters")
    private String firstName;
    @NotBlank(message = "Last name must not be blank")
    @Size(min = 2, max = 20,message = "Last Name must be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last name must start with a capital letter and contain only letters")
    private String lastName;

    @NotNull(message = "Choose gender")
    private Gender gender;

    @NotNull(message = "Birth Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    @PastOrPresent(message = "Birth Date cannot be in the future")
    private OffsetDateTime birthDate;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    private String email;
    @NotBlank(message = "Phone must not be blank")
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must be 7 to 15 digits and may start with '+'"
    )
    private String phone;


    public MemberCreateRequestDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public MemberCreateRequestDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public MemberCreateRequestDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public MemberCreateRequestDto setGender(Gender gender) {
        this.gender = gender;
        return this;
    }


    public OffsetDateTime getBirthDate() {
        return birthDate;
    }

    public MemberCreateRequestDto setBirthDate(OffsetDateTime birthDate) {
        this.birthDate = birthDate;
        return this;
    }


    public String getEmail() {
        return email;
    }

    public MemberCreateRequestDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public MemberCreateRequestDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
