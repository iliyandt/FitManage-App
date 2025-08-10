package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.domain.entity.Visit;
import demos.springdata.fitmanage.repository.VisitRepository;
import demos.springdata.fitmanage.service.VisitService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class VisitServiceImpl implements VisitService {
    private final VisitRepository visitRepository;


    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }


    @Override
    @Transactional
    public void checkIn(GymMember gymMember, Long gymId) {
        Visit visit = new Visit()
                .setGymMember(gymMember)
                .setGymId(gymId)
                .setCheckInAt(LocalDateTime.now());
        toDTO(visitRepository.save(visit));
    }

    @Override
    public List<VisitDto> getVisitsByMember(Long memberId) {
        return visitRepository.findByGymMember_Id(memberId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitDto> getVisitsInPeriod(Long id, LocalDateTime start, LocalDateTime end) {
        return visitRepository.findByGymMemberIdAndCheckInAtBetween(id ,start, end)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private VisitDto toDTO(Visit visit) {
        return new VisitDto()
                .setGymMemberId(visit.getGymMember().getId())
                .setGymId(visit.getGymId())
                .setCheckInAt(visit.getCheckInAt());
    }
}
