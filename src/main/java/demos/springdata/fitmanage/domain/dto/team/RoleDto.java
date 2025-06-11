package demos.springdata.fitmanage.domain.dto.team;

import com.fasterxml.jackson.annotation.JsonValue;
import demos.springdata.fitmanage.domain.enums.RoleType;

public class RoleDto {

    @JsonValue
    private RoleType name;

    public RoleDto() {
    }

    public RoleDto(RoleType name) {
        this.name = name;
    }

    public RoleType getName() {
        return name;
    }

    public void setName(RoleType name) {
        this.name = name;
    }
}
