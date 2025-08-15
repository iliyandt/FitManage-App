package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;
import demos.springdata.fitmanage.domain.entity.GymMember;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitService {
    public void checkIn(GymMember gymMember, Long gymId);
    public List<VisitDto> getVisitsByMember(Long memberId);
    public List<VisitTableResponse> getVisitsInPeriod(Long id, LocalDateTime start, LocalDateTime end);
}
