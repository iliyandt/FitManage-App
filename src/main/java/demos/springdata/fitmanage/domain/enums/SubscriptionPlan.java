package demos.springdata.fitmanage.domain.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import demos.springdata.fitmanage.exception.DamilSoftException;
import org.springframework.http.HttpStatus;

public enum SubscriptionPlan {
    VISIT_PASS(12, "Flexible Visit Plan"),
    MONTHLY(null, "Monthly Subscription"),
    DAY_PASS(null, "Single Visit"),
    WEEKLY_PASS(null, "Weekly Subscription"),
    BIANNUAL(null, "Biannual Subscription"),
    ANNUAL(null, "Annual Subscription");
    
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
        for (SubscriptionPlan plan : SubscriptionPlan.values()) {
            if (plan.name().equalsIgnoreCase(value) || plan.displayName.equalsIgnoreCase(value)) {
                return plan;
            }
        }

        throw new DamilSoftException("Unknown subscription plan: " + value, HttpStatus.NOT_FOUND);
    }

    @JsonValue
    public String toJson() {
        return displayName;
    }
}