package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.entity.GymMember;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitService {
    public VisitDto checkIn(GymMember gymMember, Long gymId);
    public List<VisitDto> getVisitsByMember(Long memberId);
    public List<VisitDto> getVisitsInPeriod(LocalDateTime start, LocalDateTime end);
}
