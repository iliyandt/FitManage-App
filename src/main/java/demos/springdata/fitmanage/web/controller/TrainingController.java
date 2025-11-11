package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.dto.training.TrainingRequest;
import demos.springdata.fitmanage.domain.dto.training.TrainingResponse;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.TrainingService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/trainings")
public class TrainingController {
    private final TrainingService trainingService;
    private final TableHelper tableHelper;

    public TrainingController(TrainingService trainingService, TableHelper tableHelper) {
        this.trainingService = trainingService;
        this.tableHelper = tableHelper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TrainingResponse>> create(@AuthenticationPrincipal UserData user, @RequestBody TrainingRequest request) {
        TrainingResponse response = trainingService.create(user, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TableResponseDto>> getTrainings(@AuthenticationPrincipal UserData user) {
        List<TrainingResponse> trainings = trainingService.getTrainings(user);

        TableResponseDto response = buildTableResponse(trainings);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private TableResponseDto buildTableResponse(List<TrainingResponse> trainings) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/trainings", TrainingResponse.class));
        response.setColumns(TableColumnBuilder.buildColumns(TrainingResponse.class));
        response.setRows(tableHelper.buildRows(trainings, tableHelper::buildRowMap));
        return response;
    }
}
