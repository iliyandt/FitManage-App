package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.ColumnConfigDto;
import demos.springdata.fitmanage.domain.dto.ConfigDto;
import demos.springdata.fitmanage.domain.dto.PaginationConfigDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.gym.*;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.service.GymMemberService;
import demos.springdata.fitmanage.service.GymService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@RestController
@RequestMapping(path = "/api/v1/gym")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class GymController {
    private final GymService gymService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GymController.class);
    private final GymMemberService gymMemberService;

    public GymController(GymService gymService, GymMemberService gymMemberService) {
        this.gymService = gymService;
        this.gymMemberService = gymMemberService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GymSummaryDto>> authenticatedGym() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentGymEmail = authentication.getName();
        GymSummaryDto currentGym = gymService.getGymByEmail(currentGymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found for authenticated user", ApiErrorCode.NOT_FOUND));
        ;
        return ResponseEntity.ok(ApiResponse.success(currentGym));
    }


    @PostMapping("/add_member")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> addGymMembers(@Valid @RequestBody GymMemberCreateRequestDto requestDto) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        GymMemberResponseDto responseDto = gymService.addGymMemberToGym(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @GetMapping("/gym_members_table")
    public ResponseEntity<GymMemberTableResponseDto> getAllGymMembers() {

        List<GymMemberTableDto> members = gymMemberService.findAllGymMembers();

        PaginationConfigDto pagination = new PaginationConfigDto();
        pagination.setPageSize(10);

        ConfigDto config = new ConfigDto();
        config.setTitle("Gym Members");
        config.setSortable(true);
        config.setPagination(pagination);

        List<ColumnConfigDto> columns = Arrays.stream(GymMemberCreateRequestDto.class.getDeclaredFields())
                .map(field -> new ColumnConfigDto(
                        field.getName(),
                        beautifyColumnName(field.getName())
                ))
                .toList();

        List<Map<String, String>> rows = members.stream()
                .map(member -> Map.of(
                        "firstName", member.getFirstName(),
                        "lastName", member.getLastName(),
                        "subscriptionPlan", member.getSubscriptionPlan() != null ? member.getSubscriptionPlan() : "No Subscription",
                        "phone", member.getPhone()
                ))
                .toList();

        GymMemberTableResponseDto response = new GymMemberTableResponseDto();
        response.setConfig(config);
        response.setColumns(columns);
        response.setRows(rows);

        return ResponseEntity.ok(response);
    }

    private String beautifyColumnName(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) return fieldName;

        String withSpaces = fieldName.replaceAll("([a-z])([A-Z])", "$1 $2");

        return Pattern.compile("\\b\\w")
                .matcher(withSpaces)
                .replaceAll(match -> match.group().toUpperCase())
                .trim();
    }
}
