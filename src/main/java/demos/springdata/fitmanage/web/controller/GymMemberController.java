package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.common.response.EnumOption;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.common.response.TableResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.dto.pricing.MemberPlanPriceDto;
import demos.springdata.fitmanage.domain.dto.user.UserCreateRequestDto;
import demos.springdata.fitmanage.helper.TableHelper;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.service.MemberPricingService;
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
    private final UserService userService;
    private final MemberPricingService memberPricingService;
    private final TableHelper tableHelper;
    private final static Logger LOGGER = LoggerFactory.getLogger(GymMemberController.class);

    public GymMemberController(UserService userService, MemberPricingService memberPricingService, TableHelper tableHelper) {
        this.userService = userService;
        this.memberPricingService = memberPricingService;
        this.tableHelper = tableHelper;
    }


    @GetMapping("/table")
    public ResponseEntity<ApiResponse<TableResponseDto>> getAllGymMembers(
            @ModelAttribute @Valid GymMemberFilterRequestDto filter) {

        LOGGER.debug("Fetching gym members with filter: {}", filter);

        List<GymMemberTableDto> members = (!isFilterEmpty(filter))
                ? userService.getGymMembersByFilter(filter)
                : userService.getAllGymMembersForTable();

        TableResponseDto response = buildTableResponse(members);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{gymId}/search")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> searchMember(@RequestParam String query, @PathVariable Long gymId) {
        Optional<GymMemberResponseDto> member = userService.findBySmartQuery(query, gymId);

        return member.map(gymMemberResponseDto -> ResponseEntity
                .ok(ApiResponse.success(gymMemberResponseDto))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure("Not found", "MEMBER_NOT_FOUND")));
    }


    @PostMapping("/{gymId}/check-in")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> checkInMember(
            @PathVariable Long gymId,
            @RequestParam String query) {

        GymMemberResponseDto result = userService.checkInMember(query, gymId);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/members")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> addGymMembers(@Valid @RequestBody UserCreateRequestDto requestDto) {
        LOGGER.info("Received request to create member: {}", requestDto);
        GymMemberResponseDto responseDto = userService.createAndSaveNewMember(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> updateGymMember(@PathVariable Long memberId, @Valid @RequestBody GymMemberUpdateRequestDto memberUpdateRequestDto) {
        GymMemberResponseDto updatedGymMember = userService.updateMemberDetails(memberId, memberUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedGymMember));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> deleteGymMember(@PathVariable Long memberId) {
        userService.removeGymMember(memberId);
        //todo: add response dto for delete
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{memberId}/subscription")
    public ResponseEntity<ApiResponse<GymMemberResponseDto>> updateSubscription(
            @PathVariable Long memberId,
            @RequestBody @Valid GymMemberSubscriptionRequestDto dto) {
        return ResponseEntity.ok(ApiResponse.success(userService.initializeSubscription(memberId, dto)));
    }

    @GetMapping("/subscription_plans/customized_fields")
    public ResponseEntity<ApiResponse<List<EnumOption>>> getAllSubscriptionPlans() {
        List<MemberPlanPriceDto> planPriceDtoList = memberPricingService.getPlansAndPricesAsPriceDto();
        List<EnumOption> enumOptions = planPriceDtoList.stream()
                .map(plan -> new EnumOption(plan.getSubscriptionPlan().getDisplayName(), plan.getSubscriptionPlan().toString()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(enumOptions));
    }

    private TableResponseDto buildTableResponse(List<GymMemberTableDto> members) {
        TableResponseDto response = new TableResponseDto();
        response.setConfig(tableHelper.buildTableConfig("/gym-members", GymMemberTableDto.class));
        response.setColumns(TableColumnBuilder.buildColumns(GymMemberTableDto.class));
        response.setRows(tableHelper.buildRows(members, tableHelper::buildRowMap));
        return response;
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