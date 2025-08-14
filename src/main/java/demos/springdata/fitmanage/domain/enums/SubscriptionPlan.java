package demos.springdata.fitmanage.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SubscriptionPlan {
    VISIT_PASS(12, "visit pass"),
    MONTHLY(null, "monthly"),
    DAY_PASS(null, "day pass"),
    WEEKLY_PASS(null, "weekly pass"),
    BIANNUAL(null, "biannual"),
    ANNUAL(null, "annual");
    
    private final Integer defaultVisits;
    private final String displayName;

    SubscriptionPlan(Integer defaultVisits, String displayName) {
        this.defaultVisits = defaultVisits;
        this.displayName = displayName;
    }

    public boolean isVisitBased() {
        return defaultVisits != null;
    }

    public boolean isTimeBased() {
        return !isVisitBased();
    }

    public Integer getDefaultVisits() {
        return defaultVisits;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static SubscriptionPlan fromString(String value) {
        return SubscriptionPlan.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return displayName;
    }
}