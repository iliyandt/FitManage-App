package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberSubscriptionRequestDto;
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
import demos.springdata.fitmanage.service.VisitService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GymMemberServiceImpl implements GymMemberService {
    private final GymMemberRepository gymMemberRepository;
    private final GymRepository gymRepository;
    private final RoleService roleService;
    private final VisitService visitService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(GymMemberServiceImpl.class);

    @Autowired
    public GymMemberServiceImpl(GymMemberRepository gymMemberRepository, GymRepository gymRepository, RoleService roleService, VisitService visitService, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.gymMemberRepository = gymMemberRepository;
        this.gymRepository = gymRepository;
        this.roleService = roleService;
        this.visitService = visitService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public GymMemberResponseDto createAndSaveNewMember(GymMemberCreateRequestDto requestDto) {
        String gymEmail = getAuthenticatedGymEmail();
        Gym gym = getGymByEmail(gymEmail);

        GymMember member = buildGymMember(gym, requestDto);
        validateCredentials(gym, requestDto);

        GymMember savedMember = gymMemberRepository.save(member);
        LOGGER.info("Successfully added member with ID {} to gym '{}'", savedMember.getId(), gym.getEmail());

        return mapToDto(savedMember, GymMemberResponseDto.class);
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

        validateSubscriptionChange(member, updateRequest);

        updateMemberFields(member, updateRequest);

        recalculateSubscriptionStatus(member);

        GymMember updatedMember = gymMemberRepository.save(member);
        LOGGER.info("Member with ID {} updated successfully", memberId);

        return mapToDto(updatedMember, GymMemberResponseDto.class);
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
                .map(gymMember -> mapToDto(gymMember, GymMemberTableDto.class))
                .toList();
    }


    @Override
    public Optional<GymMemberResponseDto> findBySmartQuery(String input, Long gymId) {
        return findEntityBySmartQuery(input, gymId)
                .map(member -> mapToDto(member, GymMemberResponseDto.class));
    }


    @Override
    public GymMemberResponseDto checkInMember(String input, Long gymId) {
        GymMember member = getValidatedMemberForCheckIn(input, gymId);

        if (member.getLastCheckInAt() != null && member.getLastCheckInAt().toLocalDate().isEqual(LocalDate.now())) {
            throw new IllegalStateException("Member has already checked in today.");
        }

        boolean validVisit = handleVisitPass(member);

        if (!validVisit) {
            gymMemberRepository.save(member);
            throw new FitManageAppException("No remaining visits.", ApiErrorCode.UNAUTHORIZED);
        }

        member.setLastCheckInAt(LocalDateTime.now());
        gymMemberRepository.save(member);
        visitService.checkIn(member, gymId);

        return mapToDto(member, GymMemberResponseDto.class);
    }

    @Override
    public GymMemberResponseDto initializeSubscription(Long memberId, GymMemberSubscriptionRequestDto requestDto) {

        GymMember member = getGymMemberById(memberId);

        validateAndSetSubscriptionPlan(member, requestDto);

        if (member.getSubscriptionPlan().isVisitBased()) {
            initializeVisitBasedSubscription(member, requestDto);
        } else {
            initializeTimeBasedSubscription(member);
        }

        member.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        gymMemberRepository.save(member);
        return mapToDto(member, GymMemberResponseDto.class);
    }


    private void validateAndSetSubscriptionPlan(GymMember member, GymMemberSubscriptionRequestDto requestDto) {
        SubscriptionPlan plan = requestDto.getSubscriptionPlan();

        if (plan == null) {
            throw new FitManageAppException("Subscription plan is required", ApiErrorCode.BAD_REQUEST);
        }

        member.setSubscriptionPlan(plan)
                .setEmployment(requestDto.getEmployment());
    }

    private void initializeVisitBasedSubscription(GymMember member, GymMemberSubscriptionRequestDto requestDto) {
        LOGGER.info("Visit-based subscription detected. Initializing visits...");

        Integer allowedVisits = requestDto.getAllowedVisits() != null
                ? requestDto.getAllowedVisits()
                : SubscriptionPlan.VISIT_PASS.getDefaultVisits();

        member
                .setAllowedVisits(allowedVisits)
                .setRemainingVisits(allowedVisits)
                .setSubscriptionStartDate(LocalDateTime.now())
                .setSubscriptionEndDate(null);
    }

    private void initializeTimeBasedSubscription(GymMember member) {
        LOGGER.info("Time-based subscription. Calculating expiry...");
        LocalDateTime now = LocalDateTime.now();
        member
                .setSubscriptionStartDate(now)
                .setSubscriptionEndDate(calculateEndDate(now, member.getSubscriptionPlan()))
                .setAllowedVisits(null)
                .setRemainingVisits(null);
    }


    private void recalculateSubscriptionStatus(GymMember member) {
        if (member.getSubscriptionPlan() == null) {
            member.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
            return;
        }

        if (member.getSubscriptionPlan().isVisitBased()) {
            Integer remaining = member.getRemainingVisits();
            if (remaining == null || remaining <= 0) {
                deactivateSubscription(member);
            } else {
                member.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            }
        }


    }

    private void validateSubscriptionChange(GymMember member, GymMemberUpdateRequestDto updateRequest) {
        SubscriptionPlan currentPlan = member.getSubscriptionPlan();
        SubscriptionPlan newPlan = updateRequest.getSubscriptionPlan();

        if (currentPlan == newPlan) return;

        if (currentPlan != null && currentPlan.isTimeBased()) {
            if (member.getSubscriptionEndDate() != null && LocalDateTime.now().isBefore(member.getSubscriptionEndDate())) {
                throw new FitManageAppException(
                        "Cannot change time-based plan before current period ends.",
                        ApiErrorCode.UNAUTHORIZED
                );
            }
        }

        if (currentPlan != null && currentPlan.isVisitBased()) {
            if (member.getRemainingVisits() != null && member.getRemainingVisits() > 0) {
                throw new FitManageAppException(
                        "Cannot change visit-based plan until all visits are used.",
                        ApiErrorCode.UNAUTHORIZED
                );
            }
        }
    }


    private boolean handleVisitPass(GymMember member) {
        if (member.getSubscriptionPlan() != SubscriptionPlan.VISIT_PASS) return true;

        Integer remaining = member.getRemainingVisits();
        if (remaining == null || remaining <= 0) {
            deactivateSubscription(member);
            return false;
        }

        member.setRemainingVisits(member.getRemainingVisits() - 1);
        recalculateSubscriptionStatus(member);
        return true;
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


    private <T> T mapToDto(Object source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }

    private GymMember buildGymMember(Gym gym, GymMemberCreateRequestDto requestDto) {
        GymMember member = new GymMember();

        member.setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setGender(requestDto.getGender())
                .setBirthDate(requestDto.getBirthDate())
                .setEmail(requestDto.getEmail())
                .setPhone(requestDto.getPhone())
                .setGym(gym);


        LOGGER.info("Initial password for user with email: {} will be created", member.getEmail());
        member.setPassword(passwordEncoder.encode(generateDefaultPassword()))
                .setUpdatedAt(LocalDateTime.now());

        Role gymAdminRole = roleService.findByName(RoleType.MEMBER);
        member.getRoles().add(gymAdminRole);

        return member;
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
        copyNonNullProperties(updateRequest, member);
        member.setEmployment(updateRequest.getEmployment());
    }

    private void copyNonNullProperties(GymMemberUpdateRequestDto updateRequest, GymMember member) {
        BeanUtils.copyProperties(updateRequest, member, getNullPropertyNames(updateRequest));
    }

    private String[] getNullPropertyNames(GymMemberUpdateRequestDto updateRequest) {
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(updateRequest.getClass());
        Set<String> nullProperties = new HashSet<>();

        try {
            for (PropertyDescriptor pd : pds) {
                if (pd.getReadMethod() != null) {
                    Object value = pd.getReadMethod().invoke(updateRequest);
                    if (value == null) {
                        nullProperties.add(pd.getName());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to inspect properties for null values", e);
        }
        return nullProperties.toArray(new String[0]);
    }


    private GymMember getGymMemberById(Long memberId) {
        return gymMemberRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("Member not found", ApiErrorCode.NOT_FOUND));
    }
}
