package demos.springdata.fitmanage.security;

import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.util.CurrentUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessGuard {
    private final CurrentUserUtils currentUserUtils;

    @Autowired
    public AccessGuard(CurrentUserUtils currentUserUtils) {
        this.currentUserUtils = currentUserUtils;
    }

    public boolean hasValidSubscriptionForEmployees() {
        User user = currentUserUtils.getCurrentUser();
        Abonnement subscription = user.getTenant().getAbonnement();

        return subscription == Abonnement.GROWTH;
    }
}
