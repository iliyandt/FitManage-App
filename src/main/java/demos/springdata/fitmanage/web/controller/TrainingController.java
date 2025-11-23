package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.training.TrainingRequest;
import demos.springdata.fitmanage.domain.dto.training.TrainingResponse;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.TrainingService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/trainings")
public class TrainingController {
    private final TrainingService trainingService;
    private final TableHelper tableHelper;

    public TrainingController(TrainingService trainingService, TableHelper tableHelper) {
        this.trainingService = trainingService;
        this.tableHelper = tableHelper;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PostMapping
    public ResponseEntity<ApiResponse<TrainingResponse>> create(@AuthenticationPrincipal UserData user, @RequestBody TrainingRequest request) {
        TrainingResponse response = trainingService.create(user, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'MEMBER')")
    @GetMapping
    public ResponseEntity<ApiResponse<TableResponseDto>> getTrainings(@AuthenticationPrincipal UserData user) {
        List<TrainingResponse> trainings = trainingService.getTrainings(user);
        TableResponseDto response = buildTableResponse(trainings);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @PutMapping
    public ResponseEntity<ApiResponse<TrainingResponse>> update(UUID id, @RequestBody TrainingRequest update) {
        TrainingResponse response = trainingService.update(id, update);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> update(UUID id) {
        trainingService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Training successfully deleted!"));
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping("/join/{trainingId}")
    public ResponseEntity<ApiResponse<String>> join(@AuthenticationPrincipal UserData user, @PathVariable UUID trainingId) {
        trainingService.joinTraining(user, trainingId);
        return ResponseEntity.ok(ApiResponse.success("Joined"));
    }

    @PreAuthorize("hasAuthority('MEMBER')")
    @PostMapping("/cancel/{trainingId}")
    public ResponseEntity<ApiResponse<String>> cancel(@AuthenticationPrincipal UserData user, @PathVariable UUID trainingId) {
        trainingService.cancelTraining(user, trainingId);

        return ResponseEntity.ok(ApiResponse.success("Canceled"));
    }

    private TableResponseDto buildTableResponse(List<TrainingResponse> trainings) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/trainings", TrainingResponse.class));
        response.setColumns(TableColumnBuilder.buildColumns(TrainingResponse.class));
        response.setRows(tableHelper.buildRows(trainings, tableHelper::buildRowMap));
        return response;
    }
}
