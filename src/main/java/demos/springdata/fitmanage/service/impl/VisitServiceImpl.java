package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;

import demos.springdata.fitmanage.domain.entity.Membership;

import demos.springdata.fitmanage.domain.entity.Visit;
import demos.springdata.fitmanage.repository.VisitRepository;
import demos.springdata.fitmanage.service.VisitService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;



@Service
public class VisitServiceImpl implements VisitService {
    private final VisitRepository visitRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitServiceImpl.class);


    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }


    @Override
    @Transactional
    public void checkIn(Membership membership, Long userId) {

        LOGGER.info("Check-in at gymId={} for memberId={} ({} {})",
                userId,
                membership.getId(),
                membership.getFirstName(),
                membership.getLastName());

        Visit visit = new Visit()
                .setMembership(membership)
                .setUserId(userId)
                .setCheckInAt(LocalDateTime.now());

        Visit savedVisit = visitRepository.save(visit);
        LOGGER.info("Check-in successful: memberId={}, gymId={}, time={}",
                savedVisit.getMembership().getId(),
                savedVisit.getUserId(),
                savedVisit.getCheckInAt());
        toDTO(savedVisit);
    }



    @Override
    @Transactional
    public List<VisitTableResponse> getVisitsInPeriod(Long gymId, LocalDateTime start, LocalDateTime end) {

        return List.of();
    }



    private VisitTableResponse manualMapDto(Visit visit) {

        Membership membership = visit.getMembership();

        VisitTableResponse dto = new VisitTableResponse();
        dto.setId(visit.getId())
                .setFirstName(membership.getFirstName())
                .setLastName(membership.getLastName())
                .setPhone(membership.getUser().getPhone())
                .setSubscriptionPlan(membership.getSubscriptionPlan());

        return dto;
    }

    private VisitDto toDTO(Visit visit) {
        return new VisitDto()
                .setMemberId(visit.getMembership().getId())
                .setGymId(visit.getUserId())
                .setCheckInAt(visit.getCheckInAt());
    }
}
