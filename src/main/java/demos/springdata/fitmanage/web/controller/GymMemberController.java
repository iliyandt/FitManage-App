package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.ColumnConfigDto;
import demos.springdata.fitmanage.domain.dto.ConfigDto;
import demos.springdata.fitmanage.domain.dto.PaginationConfigDto;
import demos.springdata.fitmanage.domain.dto.gym.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gym.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gym.GymMemberTableResponseDto;
import demos.springdata.fitmanage.service.GymMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/api/v1/gym/members")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class GymMemberController {


    private final GymMemberService gymMemberService;

    public GymMemberController(GymMemberService gymMemberService) {
        this.gymMemberService = gymMemberService;
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

        List<ColumnConfigDto> columns = Arrays.stream(GymMemberTableDto.class.getDeclaredFields())
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
