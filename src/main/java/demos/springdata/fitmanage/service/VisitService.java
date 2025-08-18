package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;
import demos.springdata.fitmanage.domain.entity.Membership;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitService {
    public void checkIn(Membership membership, Long gymId);
    public List<VisitTableResponse> getVisitsInPeriod(Long gymId, LocalDateTime start, LocalDateTime end);
}
