package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.entity.GymMember;
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
import java.util.stream.Collectors;


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
    public void checkIn(GymMember gymMember, Long gymId) {

        LOGGER.info("Check-in at gymId={} for memberId={} ({} {})",
                gymId,
                gymMember.getId(),
                gymMember.getFirstName(),
                gymMember.getLastName());

        Visit visit = new Visit()
                .setGymMember(gymMember)
                .setGymId(gymId)
                .setCheckInAt(LocalDateTime.now());

        Visit savedVisit = visitRepository.save(visit);
        LOGGER.info("Check-in successful: memberId={}, gymId={}, time={}",
                savedVisit.getGymMember().getId(),
                savedVisit.getGymId(),
                savedVisit.getCheckInAt());
        toDTO(savedVisit);
    }

    @Override
    public List<VisitDto> getVisitsByMember(Long memberId) {
        LOGGER.info("Fetching visits: memberId={}", memberId);
        List<VisitDto> visits = visitRepository.findByGymMember_Id(memberId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        LOGGER.info("Found {} visits for memberId={}", visits.size(), memberId);
        return visits;
    }

    @Override
    public List<VisitDto> getVisitsInPeriod(Long memberId, LocalDateTime start, LocalDateTime end) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LOGGER.info("Fetching visits: memberId={}, start={}, end={}",
                memberId,
                start.format(formatter),
                end.format(formatter));

        List<VisitDto> visits = visitRepository.findVisitsBetweenDates(memberId, start, end)
                .stream()
                .map(this::toDTO)
                .toList();


        LOGGER.info("Found {} visits for memberId={} in period {} to {}",
                visits.size(),
                memberId,
                start.format(formatter),
                end.format(formatter));

        return visits;
    }

    private VisitDto toDTO(Visit visit) {
        return new VisitDto()
                .setMemberId(visit.getGymMember().getId())
                .setGymId(visit.getGymId())
                .setCheckInAt(visit.getCheckInAt());
    }
}
