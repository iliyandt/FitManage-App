package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.GymMemberService;
import demos.springdata.fitmanage.util.TableColumnBuilder;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/api/v1/gym-members")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class GymMemberController {
    private final GymMemberService gymMemberService;
    private final TableHelper tableHelper;
    private final static Logger LOGGER = LoggerFactory.getLogger(GymMemberController.class);

    public GymMemberController(GymMemberService gymMemberService, TableHelper tableHelper) {
        this.gymMemberService = gymMemberService;
        this.tableHelper = tableHelper;
    }


    @GetMapping("/table")
    public ResponseEntity<ApiResponse<TableResponseDto>> getAllGymMembers(@RequestParam(required = false) String firstName,
                                                                          @RequestParam(required = false) String lastName,
                                                                          @RequestParam(required = false) Gender gender,
                                                                          @RequestParam(required = false) Employment employment,
                                                                          @RequestParam(required = false) SubscriptionStatus subscriptionStatus,
                                                                          @RequestParam(required = false) String email) {

        GymMemberFilterRequestDto filter = new GymMemberFilterRequestDto()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setGender(gender)
                .setEmployment(employment)
                .setSubscriptionStatus(subscriptionStatus)
                .setEmail(email);


        List<GymMemberTableDto> members = (!isFilterEmpty(filter))
                ? gymMemberService.getGymMembersByFilter(filter)
                : gymMemberService.getAllGymMembersForTable();

        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/gym-members", GymMemberTableDto.class));
        response.setColumns(TableColumnBuilder.buildColumns(GymMemberTableDto.class));
        response.setRows(tableHelper.buildRows(members, tableHelper::buildRowMap));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{gymId}/search")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> searchMember(@RequestParam String query, @PathVariable Long gymId) {
        Optional<GymMemberResponseDto> member = gymMemberService.findBySmartQuery(query, gymId);

        return member.map(gymMemberResponseDto -> ResponseEntity
                .ok(ApiResponse.success(gymMemberResponseDto))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure("Not found", "MEMBER_NOT_FOUND")));
    }


    @PostMapping("/{gymId}/check-in")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> checkInMember(
            @PathVariable Long gymId,
            @RequestParam String query) {

        GymMemberResponseDto result = gymMemberService.checkInMember(query, gymId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/members")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> addGymMembers(@Valid @RequestBody GymMemberCreateRequestDto requestDto) {
        LOGGER.info("Received request to create member: {}", requestDto);
        GymMemberResponseDto responseDto = gymMemberService.createAndSaveNewMember(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> updateGymMember(@PathVariable Long memberId, @Valid @RequestBody GymMemberUpdateRequestDto memberUpdateRequestDto) {
        GymMemberResponseDto updatedGymMember = gymMemberService.updateMemberDetails(memberId, memberUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedGymMember));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteGymMember(@PathVariable Long memberId) {
        gymMemberService.removeGymMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{memberId}/subscription")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> updateSubscription(
            @PathVariable Long memberId,
            @RequestBody @Valid GymMemberSubscriptionRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(gymMemberService.initializeSubscription(memberId, dto)));
    }


    private boolean isFilterEmpty(GymMemberFilterRequestDto filter) {
        return filter.getFirstName() == null &&
                filter.getLastName() == null &&
                filter.getGender() == null &&
                filter.getEmployment() == null &&
                filter.getBirthDate() == null &&
                filter.getVisitLimit() == null &&
                filter.getEmail() == null &&
                filter.getSubscriptionStatus() == null &&
                filter.getSubscriptionPlan() == null &&
                filter.getPhone() == null;
    }
}
