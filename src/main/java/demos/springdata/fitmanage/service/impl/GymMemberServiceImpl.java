package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.support.GymMemberSpecification;
import demos.springdata.fitmanage.service.GymMemberService;
import demos.springdata.fitmanage.service.RoleService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GymMemberServiceImpl implements GymMemberService {
    private final GymMemberRepository gymMemberRepository;
    private final GymRepository gymRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(GymMemberServiceImpl.class);

    @Autowired
    public GymMemberServiceImpl(GymMemberRepository gymMemberRepository, GymRepository gymRepository, RoleService roleService, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.gymMemberRepository = gymMemberRepository;
        this.gymRepository = gymRepository;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public GymMemberResponseDto createAndSaveNewMember(GymMemberCreateRequestDto requestDto) {
        String gymEmail = getAuthenticatedGymEmail();
        Gym gym = getGymByEmail(gymEmail);

        GymMember member = buildGymMember(gym, requestDto);
        validateCredentials(gym, requestDto);

        GymMember savedMember = gymMemberRepository.save(member);
        LOGGER.info("Successfully added member with ID {} to gym '{}'", savedMember.getId(), gym.getEmail());

        return modelMapper.map(savedMember, GymMemberResponseDto.class);
    }

    @Override
    public List<GymMemberTableDto> getAllGymMembersForTable() {
        String gymEmail = getAuthenticatedGymEmail();
        Gym gym = getGymByEmail(gymEmail);

        List<GymMember> members = gymMemberRepository.findGymMembersByGym(gym);

        return members.stream()
                .map(member -> modelMapper.map(member, GymMemberTableDto.class))
                .toList();
    }


    @Override
    public GymMemberResponseDto updateMemberDetails(Long memberId, GymMemberUpdateRequestDto updateRequest) {
        GymMember member = getGymMemberById(memberId);

        validateUpdatedPhone(member, updateRequest.getPhone());
        updateMemberFields(member, updateRequest);

        GymMember updatedMember = gymMemberRepository.save(member);
        LOGGER.info("Member with ID {} updated successfully", memberId);

        return mapToResponseDto(updatedMember);
    }


    @Override
    public void removeGymMember(Long memberId) {
        GymMember gymMember = gymMemberRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("Gym member not found", ApiErrorCode.NOT_FOUND));
        LOGGER.info("Deleting member with ID {}", memberId);

        gymMemberRepository.delete(gymMember);
        LOGGER.info("Member with ID {} deleted successfully", memberId);
    }

    @Override
    public List<GymMemberTableDto> getGymMembersByFilter(GymMemberFilterRequestDto filter) {
        LOGGER.info("Search for users with filter: {}", filter);

        Gym gym = getGymByEmail(getAuthenticatedGymEmail());

        Specification<GymMember> spec = GymMemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("gym"), gym));

        List<GymMember> memberList = gymMemberRepository.findAll(spec);


        if (memberList.isEmpty())
            throw new FitManageAppException("No members found for the given filter", ApiErrorCode.NOT_FOUND);

        return memberList
                .stream()
                .map(gymMember -> modelMapper.map(gymMember, GymMemberTableDto.class))
                .toList();
    }



    @Override
    public Optional<GymMemberResponseDto> findBySmartQuery(String input, Long gymId) {
        return findEntityBySmartQuery(input, gymId)
                .map(gymMember -> modelMapper.map(gymMember, GymMemberResponseDto.class));
    }


    @Override
    public GymMemberResponseDto checkInMember(String input, Long gymId) {
        GymMember member = getValidatedMemberForCheckIn(input, gymId);

        handleVisitPass(member);

        member.setLastCheckInAt(LocalDateTime.now());

        gymMemberRepository.save(member);
        return modelMapper.map(member, GymMemberResponseDto.class);
    }


    private void handleVisitPass(GymMember member) {
        if (member.getSubscriptionPlan() != SubscriptionPlan.VISIT_PASS) return;

        Integer remaining = member.getRemainingVisits();
        if (remaining == null || remaining <= 0) {
            deactivateSubscription(member);
            throw new FitManageAppException("No remaining visits.", ApiErrorCode.UNAUTHORIZED);
        }

        member.setRemainingVisits(member.getRemainingVisits() - 1);
    }

    private void deactivateSubscription(GymMember member) {
        member.setSubscriptionStatus(SubscriptionStatus.INACTIVE)
                .setSubscriptionPlan(null);
    }

    private GymMember getValidatedMemberForCheckIn(String input, Long gymId) {
        return findEntityBySmartQuery(input, gymId)
                .filter(this::hasActiveSubscription)
                .orElseThrow(() -> new FitManageAppException(
                        "Member not found or does not have an active subscription.",
                        ApiErrorCode.UNAUTHORIZED
                ));
    }

    private boolean hasActiveSubscription(GymMember member) {
        return member.getSubscriptionStatus() == SubscriptionStatus.ACTIVE;
    }


    private Optional<GymMember> findEntityBySmartQuery(String input, Long gymId) {
        try {
            Long id = Long.parseLong(input);
            Optional<GymMember> byId = gymMemberRepository.findByIdAndGym_Id(id, gymId);
            if (byId.isPresent()) return byId;
        } catch (NumberFormatException ignored) {
        }

        Optional<GymMember> byPhone = gymMemberRepository.findByPhoneIgnoreCaseAndGym_Id(input, gymId);
        if (byPhone.isPresent()) return byPhone;

        Optional<GymMember> byEmail = gymMemberRepository.findByEmailIgnoreCaseAndGym_Id(input, gymId);
        if (byEmail.isPresent()) return byEmail;

        String[] parts = input.trim().split("\\s+");
        if (parts.length >= 2) {
            return gymMemberRepository
                    .findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndGym_Id(parts[0], parts[1], gymId);
        }

        LOGGER.warn("Check-in failed: No match found for input '{}' in gym '{}'", input, gymId);
        return Optional.empty();
    }


    private GymMemberResponseDto mapToResponseDto(GymMember updatedMember) {
        return modelMapper.map(updatedMember, GymMemberResponseDto.class);
    }

    private GymMember buildGymMember(Gym gym, GymMemberCreateRequestDto requestDto) {
        GymMember member = modelMapper.map(requestDto, GymMember.class);

        LOGGER.info("Initial password for user with email: {} will be created", member.getEmail());
        member.setPassword(passwordEncoder.encode(generateDefaultPassword()))
                .setUpdatedAt(LocalDateTime.now())
                .setGym(gym);

        Role gymAdminRole = roleService.findByName(RoleType.MEMBER);
        member.getRoles().add(gymAdminRole);
        initializeSubscription(member, requestDto);

        return member;
    }

    private void initializeSubscription(GymMember member, GymMemberCreateRequestDto requestDto) {
        LOGGER.info("Checking if subscription plan is chosen..");

        if (member.getSubscriptionPlan() == null) {
            member.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
            return;
        }

        SubscriptionPlan plan = member.getSubscriptionPlan();


        if (plan.isVisitBased()) {
            LOGGER.info("Visit-based subscription detected. Initializing visits...");

            Integer allowedVisits = requestDto.getVisitLimit() != null
                    ? requestDto.getVisitLimit()
                    : SubscriptionPlan.VISIT_PASS.getDefaultVisits();

            member.setAllowedVisits(allowedVisits)
                    .setRemainingVisits(allowedVisits)
                    .setSubscriptionStartDate(LocalDateTime.now())
                    .setSubscriptionEndDate(null);
        } else {
            LOGGER.info("Time-based subscription. Calculating expiry...");
            LocalDateTime now = LocalDateTime.now();
            member.setSubscriptionStartDate(now)
                    .setSubscriptionEndDate(calculateEndDate(now, plan))
                    .setAllowedVisits(null)
                    .setRemainingVisits(null);
        }

        member.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
    }

    private LocalDateTime calculateEndDate(LocalDateTime start, SubscriptionPlan subscriptionPlan) {
        return switch (subscriptionPlan) {
            case MONTHLY -> start.plusMonths(1);
            case DAY_PASS -> start.plusMinutes(180);
            case WEEKLY_PASS -> start.plusWeeks(1);
            case BIANNUAL -> start.plusMonths(6);
            case ANNUAL -> start.plusYears(1);
            default -> throw new IllegalArgumentException("Unhandled subscription plan: " + subscriptionPlan);
        };
    }

    private String generateDefaultPassword() {
        return "GymMember" + System.currentTimeMillis() + "!";
    }

    private Gym getGymByEmail(String gymEmail) {
        return gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> {
                    LOGGER.warn("Gym with email {} not found.", gymEmail);
                    return new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND);
                });
    }

    private String getAuthenticatedGymEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        LOGGER.info("Authenticated gym email: {}", email);
        return email;
    }

    private void validateCredentials(Gym gym, GymMemberCreateRequestDto requestDto) {
        Map<String, String> errors = new HashMap<>();

        if (gymMemberRepository.existsByEmailAndGymEmail(requestDto.getEmail(), gym.getEmail())) {
            LOGGER.warn("Member with email {} already exists", gym.getEmail());
            errors.put("email", "Email is already registered");
        }

        if (gymMemberRepository.existsByPhoneAndGymEmail(requestDto.getPhone(), gym.getEmail())) {
            LOGGER.warn("Member with phone {} already exists", gym.getPhone());
            errors.put("phone", "Phone used from another member");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }

    private void validateUpdatedPhone(GymMember member, String newPhone) {
        boolean isPhoneChanged = !member.getPhone().equals(newPhone);
        boolean phoneExists = gymMemberRepository.existsByPhone(newPhone);

        if (isPhoneChanged && phoneExists) {
            throw new MultipleValidationException(Map.of("phone", "Phone used by another member"));
        }
    }

    private void updateMemberFields(GymMember member, GymMemberUpdateRequestDto updateRequest) {
        LOGGER.info("Updating member with ID {}", member.getId());
        member.setFirstName(updateRequest.getFirstName())
                .setLastName(updateRequest.getLastName())
                .setEmail(updateRequest.getEmail())
                .setGender(updateRequest.getGender())
                .setEmployment(updateRequest.getEmployment())
                .setAllowedVisits(updateRequest.getVisitLimit())
                .setBirthDate(updateRequest.getBirthDate())
                .setSubscriptionPlan(updateRequest.getSubscriptionPlan())
                .setSubscriptionStatus(updateRequest.getSubscriptionStatus())
                .setPhone(updateRequest.getPhone())
                .setUpdatedAt(LocalDateTime.now());
    }

    private GymMember getGymMemberById(Long memberId) {
        return gymMemberRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("Member not found", ApiErrorCode.NOT_FOUND));
    }
}
