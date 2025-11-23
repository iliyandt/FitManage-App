package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Visit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitService {
    List<VisitDto> findVisitsForMember(UUID memberId);
    Visit checkIn(Membership membership, UUID memberId);
    List<VisitTableResponse> getVisitsInPeriod(UUID clubId, LocalDateTime start, LocalDateTime end);
}
