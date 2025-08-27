package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Visit;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.VisitRepository;
import demos.springdata.fitmanage.service.VisitService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VisitServiceImpl implements VisitService {
    private final VisitRepository visitRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitServiceImpl.class);
    private final ModelMapper modelMapper;

    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository, ModelMapper modelMapper) {
        this.visitRepository = visitRepository;
        this.modelMapper = modelMapper;
    }

    //TODO: What information to return in the DTO?
    @Override
    public List<VisitDto> findVisitsForMember(Long memberId) {
        List<Visit> visitList = visitRepository.findByUser_Id(memberId)
                .orElseThrow(() -> new FitManageAppException("No visits for this member", ApiErrorCode.NOT_FOUND));

        return visitList.stream().map(v -> modelMapper.map(v, VisitDto.class)).toList();
    }

    @Override
    @Transactional
    public Visit checkIn(Membership membership, Long memberId) {

        LOGGER.info("Check-in at tenantId={} for memberId={} ({} {})",
                membership.getTenant().getId(),
                memberId,
                membership.getUser().getFirstName(),
                membership.getUser().getLastName());

        Visit visit = new Visit()
                .setMembership(membership)
                .setUser(membership.getUser());

        Visit savedVisit = visitRepository.save(visit);
        LOGGER.info("Check-in successful: memberId={}, time={}",
                savedVisit.getMembership().getId(),
                savedVisit.getCheckInAt());

        return savedVisit;
    }

    //TODO: add logic
    @Override
    @Transactional
    public List<VisitTableResponse> getVisitsInPeriod(Long gymId, LocalDateTime start, LocalDateTime end) {
        return List.of();
    }

    private VisitTableResponse manualMapDto(Visit visit) {
        Membership membership = visit.getMembership();

        VisitTableResponse dto = new VisitTableResponse();
        dto.setId(visit.getId())
                .setFirstName(membership.getUser().getFirstName())
                .setLastName(membership.getUser().getLastName())
                .setPhone(membership.getUser().getPhone())
                .setSubscriptionPlan(membership.getSubscriptionPlan());

        return dto;
    }
}
