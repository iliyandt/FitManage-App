package demos.springdata.fitmanage.domain.dto.users;


import com.fasterxml.jackson.annotation.JsonFormat;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdate;
import demos.springdata.fitmanage.domain.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdate {
    @Size(min = 2, max = 15, message = "First Name must be between 2 and 15 characters")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "First name must start with a capital letter and contain only letters")
    private String firstName;
    @Size(min = 2, max = 30, message = "Last Name must be between 2 and 30 characters")
    @Pattern(regexp = "^[A-Z][a-zA-Z]*$", message = "Last name must start with a capital letter and contain only letters")
    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    @PastOrPresent(message = "Birth Date cannot be in the future")
    private OffsetDateTime birthDate;
    @Size(min = 2, max = 15, message = "Username must be  between 2 and 15 characters")
    private String username;
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be 7 to 15 digits and may start with '+'")
    private String phone;
    private String address;
    private String city;
    private Gender gender;
    private String email;
    private MemberUpdate memberUpdate;
}
