package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.service.SubscriptionMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionMonitorServiceImpl implements SubscriptionMonitorService {

    private GymMemberRepository gymMemberRepository;

    @Autowired
    public SubscriptionMonitorServiceImpl(GymMemberRepository gymMemberRepository) {
        this.gymMemberRepository = gymMemberRepository;
    }

    @Override
    @Scheduled(cron = "0 */5 * * * *")

    public void deactivateExpiredSubscriptions() {
        List<GymMember> activeMembers = gymMemberRepository.findBySubscriptionStatus(SubscriptionStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        for (GymMember member : activeMembers) {
            if (member.getSubscriptionEndDate().isBefore(now)) {
                member.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
                gymMemberRepository.save(member);
            }
        }
    }
}
