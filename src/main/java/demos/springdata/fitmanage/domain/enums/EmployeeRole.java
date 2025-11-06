package demos.springdata.fitmanage.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EmployeeRole {
    MANAGER("Manager"),
    NUTRITIONIST("Nutritionist"),
    MASSAGE_THERAPIST("Massage Therapist"),
    TRAINER("Trainer"),
    RECEPTIONIST("Receptionist"),
    CLEANER("Cleaner"),
    PHYSIOTHERAPIST("Physiotherapist");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
