package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.service.VisitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/visits")
public class VisitController {
    private final VisitService visitService;


    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping("/member/{gymMemberId}")
    public ResponseEntity<ApiResponse<List<VisitDto>>> getVisitsByMember(@PathVariable Long gymMemberId) {
        List<VisitDto> visitsByMember = visitService.getVisitsByMember(gymMemberId);

        return ResponseEntity.ok(ApiResponse.success(visitsByMember));
    }

    @GetMapping("/period/{id}/{start}/{end}")
    public ResponseEntity<ApiResponse<List<VisitDto>>> getVisitsInPeriod(@PathVariable Long id, @PathVariable String start, @PathVariable String end) {
        LocalDateTime startDateTime = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(end).atTime(LocalTime.MAX);
        List<VisitDto> visitsInPeriod = visitService.getVisitsInPeriod(id, startDateTime, endDateTime);

        return ResponseEntity.ok(ApiResponse.success(visitsInPeriod));
    }

}
