package demos.springdata.fitmanage.schedular;

import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PlanScheduler {
    private final MembershipRepository membershipRepository;

    @Autowired
    public PlanScheduler(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deactivateExpiredPlans() {
        LocalDateTime now = LocalDateTime.now();

        List<Membership> expiredMemberships = membershipRepository.findAllBySubscriptionStatus(SubscriptionStatus.ACTIVE)
                .stream()
                .filter(membership -> membership.getSubscriptionPlan().isTimeBased())
                .filter(membership -> membership.getSubscriptionEndDate().isBefore(now))
                .toList();

        if (!expiredMemberships.isEmpty()) {
            expiredMemberships.forEach(membership -> {
                membership
                        .setSubscriptionStatus(SubscriptionStatus.INACTIVE)
                        .setSubscriptionPlan(null)
                        .setSubscriptionStartDate(null)
                        .setSubscriptionEndDate(null);
            });

            membershipRepository.saveAll(expiredMemberships);
        }
    }
}
