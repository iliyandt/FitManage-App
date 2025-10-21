package demos.springdata.fitmanage.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Employment {
    REGULAR("Regular"),
    STUDENT("Student"),
    SENIOR("Senior"),
    HANDICAP("Handicap");

    private final String displayName;

    Employment(String displayName) {
        this.displayName = displayName;
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
