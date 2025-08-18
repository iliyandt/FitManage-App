package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitDto;
import demos.springdata.fitmanage.domain.dto.visit.VisitTableResponse;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.VisitService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/visits")
public class VisitController {
    private final VisitService visitService;
    private final TableHelper tableHelper;


    public VisitController(VisitService visitService, TableHelper tableHelper) {
        this.visitService = visitService;
        this.tableHelper = tableHelper;
    }

    @GetMapping("/member/{gymMemberId}")
    public ResponseEntity<ApiResponse<List<VisitDto>>> getVisitsByMember(@PathVariable Long gymMemberId) {
        return null;
    }

    @GetMapping("/period/{id}/{start}/{end}")
    public ResponseEntity<ApiResponse<TableResponseDto>> getVisitsInPeriod(@PathVariable Long id, @PathVariable String start, @PathVariable String end) {
        LocalDateTime startDateTime = LocalDate.parse(start).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(end).plusDays(1).atStartOfDay();
        List<VisitTableResponse> visitsInPeriod = visitService.getVisitsInPeriod(id, startDateTime, endDateTime);

        TableResponseDto response = buildTableResponse(visitsInPeriod);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    private TableResponseDto buildTableResponse(List<VisitTableResponse> members) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/visits", VisitTableResponse.class));
        response.setColumns(TableColumnBuilder.buildColumns(VisitTableResponse.class));
        response.setRows(tableHelper.buildRows(members, tableHelper::buildRowMap));
        return response;
    }

}
