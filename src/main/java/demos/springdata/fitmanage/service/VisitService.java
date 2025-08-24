package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Visit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitService {
    List<VisitDto> findVisitsForMember(Long memberId);
    Visit checkIn(Membership membership, Long memberId);
    List<VisitTableResponse> getVisitsInPeriod(Long gymId, LocalDateTime start, LocalDateTime end);
}
