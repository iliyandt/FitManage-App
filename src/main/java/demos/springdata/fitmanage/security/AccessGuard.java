package demos.springdata.fitmanage.security;

import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class AccessGuard {
    private final UserService userService;

    @Autowired
    public AccessGuard(UserService userService) {
        this.userService = userService;
    }

    public boolean hasValidSubscription(String abonnement) {
        User user = userService.getCurrentUser();
        Abonnement subscription = user.getTenant().getAbonnement();
        return subscription == Abonnement.valueOf(abonnement);
    }
}
