package demos.springdata.fitmanage.domain.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleType {
    ADMINISTRATOR("Administrator"),
    ADMIN("Admin"),
    MEMBER("Member"),
    STAFF("Staff");

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

