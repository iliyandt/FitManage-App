package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.entity.Visit;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.VisitRepository;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.service.VisitService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitServiceImpl implements VisitService {
    private final VisitRepository visitRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitServiceImpl.class);
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository, ModelMapper modelMapper, UserService userService) {
        this.visitRepository = visitRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
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

    @Override
    @Transactional
    public List<VisitTableResponse> getVisitsInPeriod(Long facilityAdminId, LocalDateTime start, LocalDateTime end) {
        User user = userService.findUserById(facilityAdminId);
        Tenant tenant = user.getTenant();

        List<Visit> visits = visitRepository.findByUserTenantAndCheckInAtBetween(tenant ,start, end);

        return visits.stream().map(this::manualMapDto).toList();
    }

    private VisitTableResponse manualMapDto(Visit visit) {
        Membership membership = visit.getMembership();

        VisitTableResponse dto = new VisitTableResponse();

        dto.setId(visit.getUser().getId())
                .setFirstName(membership.getUser().getFirstName())
                .setLastName(membership.getUser().getLastName())
                .setPhone(membership.getUser().getPhone())
                .setSubscriptionPlan(membership.getSubscriptionPlan());

        return dto;
    }
}
