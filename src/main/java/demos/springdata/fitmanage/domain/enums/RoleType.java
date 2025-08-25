package demos.springdata.fitmanage.domain.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleType {
    SYSTEM_ADMIN("System Admin"),
    FACILITY_ADMIN("Facility Admin"),
    FACILITY_MEMBER("Facility Member"),
    FACILITY_STAFF("Facility Staff");

    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static RoleType fromString(String value) {
        return RoleType.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return displayName;
    }



}

