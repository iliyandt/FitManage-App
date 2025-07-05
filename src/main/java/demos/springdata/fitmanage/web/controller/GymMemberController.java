package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.ActionConfigDto;
import demos.springdata.fitmanage.domain.dto.ColumnConfigDto;
import demos.springdata.fitmanage.domain.dto.ConfigDto;
import demos.springdata.fitmanage.domain.dto.PaginationConfigDto;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberTableResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.service.GymMemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/api/v1/gym-members")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class GymMemberController {


    private final GymMemberService gymMemberService;

    public GymMemberController(GymMemberService gymMemberService) {
        this.gymMemberService = gymMemberService;
    }


    @GetMapping("/table")
    public ResponseEntity<GymMemberTableResponseDto> getAllGymMembers() {
        List<GymMemberTableDto> members = gymMemberService.findAllGymMembers();

        GymMemberTableResponseDto response = new GymMemberTableResponseDto();
        response.setConfig(buildTableConfig());
        response.setColumns(buildColumns(GymMemberTableDto.class));
        response.setRows(buildRows(members));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> updateGymMember(@PathVariable Long memberId, @Valid @RequestBody GymMemberUpdateRequestDto memberUpdateRequestDto) {
        GymMemberResponseDto updatedGymMember = gymMemberService.updateGymMember(memberId, memberUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedGymMember));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteGymMember(@PathVariable Long memberId) {
        gymMemberService.deleteGymMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }


    private List<Map<String, Object>> buildRows(List<GymMemberTableDto> members) {
        return members.stream()
                .map(this::buildRowMap)
                .toList();
    }

    private Map<String, Object> buildRowMap(GymMemberTableDto member) {
        Map<String, Object> row = new LinkedHashMap<>();

        row.put("id", member.getId());
        row.put("firstName", member.getFirstName());
        row.put("lastName", member.getLastName());
        row.put("subscriptionPlan", member.getSubscriptionPlan() != null ? member.getSubscriptionPlan() : "No Subscription");
        row.put("phone", member.getPhone());

        return row;
    }

    private List<ColumnConfigDto> buildColumns(Class<GymMemberTableDto> gymMemberTableDtoClass) {
        return Arrays.stream(GymMemberTableDto.class.getDeclaredFields())
                .map(field -> new ColumnConfigDto(
                        field.getName(),
                        beautifyColumnName(field.getName())
                ))
                .toList();
    }

    private ConfigDto buildTableConfig() {
        PaginationConfigDto pagination = new PaginationConfigDto();
        pagination.setPageSize(10);

        List<ActionConfigDto> actions = List.of(
                new ActionConfigDto("details", "Details", "gym-members/{id}"),
                new ActionConfigDto("edit", "Edit", "gym-members/{id}"),
                new ActionConfigDto("delete", "Delete", "gym-members/{id}")
        );

        ConfigDto config = new ConfigDto();
        config.setSortable(true);
        config.setActions(actions);
        config.setPagination(pagination);

        return config;
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
