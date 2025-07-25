package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.service.SubscriptionMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionMonitorServiceImpl implements SubscriptionMonitorService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubscriptionMonitorServiceImpl.class);
    private GymMemberRepository gymMemberRepository;

    @Autowired
    public SubscriptionMonitorServiceImpl(GymMemberRepository gymMemberRepository) {
        this.gymMemberRepository = gymMemberRepository;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void deactivateExpiredSubscriptions() {
        LOGGER.info("Subscription monitoring started at {}", LocalDateTime.now());
        List<GymMember> activeMembers = gymMemberRepository.findBySubscriptionStatus(SubscriptionStatus.ACTIVE);

        LOGGER.info("Found {} active members", activeMembers.size());
        LocalDateTime now = LocalDateTime.now();
        for (GymMember member : activeMembers) {

            LocalDateTime endDate = member.getSubscriptionEndDate();

            if (endDate == null) {
                LOGGER.debug("Skipping member ID {} - no subscription end date", member.getId());
                continue;
            }


            if (endDate.isBefore(now)) {
                LOGGER.info("Deactivating expired member ID {} - email: {}", member.getId(), member.getEmail());
                member.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
                gymMemberRepository.save(member);
            }
        }
    }
}
