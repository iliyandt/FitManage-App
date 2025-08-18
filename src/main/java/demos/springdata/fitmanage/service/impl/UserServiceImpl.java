package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.dto.user.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.MembershipRepository;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.repository.support.GymMemberSpecification;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.service.VisitService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final TenantRepository tenantRepository;
    private final MembershipRepository membershipRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder, TenantRepository tenantRepository, MembershipRepository membershipRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;

        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.tenantRepository = tenantRepository;
        this.membershipRepository = membershipRepository;
    }


    @Override
    @Transactional
    public GymMemberResponseDto createAndSaveNewMember(UserCreateRequestDto requestDto) {
        String userEmail = getAuthenticatedGymEmail();
        Tenant tenant = getTenantByEmail(userEmail);

        User member = buildGymMember(tenant, requestDto);
        validateCredentials(member, requestDto);

        User user = userRepository.save(member);
        LOGGER.info("Successfully added member with ID {} to gym '{}'", user.getId(), tenant.getName());

        return mapToDto(user, GymMemberResponseDto.class);
    }

    @Override
    public List<GymMemberTableDto> getAllGymMembersForTable() {
        String gymEmail = getAuthenticatedGymEmail();
        Tenant tenant = getTenantByEmail(gymEmail);

        List<User> members = tenant.getUsers();

        return members.stream()
                .map(member -> modelMapper.map(member, GymMemberTableDto.class))
                .toList();
    }


    @Override
    public GymMemberResponseDto updateMemberDetails(Long memberId, GymMemberUpdateRequestDto updateRequest) {
        return null;
    }


    @Override
    public void removeGymMember(Long memberId) {
        User user = userRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("Gym member not found", ApiErrorCode.NOT_FOUND));
        LOGGER.info("Deleting member with ID {}", memberId);

        userRepository.delete(user);

        LOGGER.info("Member with ID {} deleted successfully", memberId);
    }

    @Override
    public List<GymMemberTableDto> getGymMembersByFilter(GymMemberFilterRequestDto filter) {
        LOGGER.info("Search for users with filter: {}", filter);

        Tenant tenant = getTenantByEmail(getAuthenticatedGymEmail());

        Specification<Membership> spec = GymMemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("gym"), tenant));

        List<Membership> memberList = membershipRepository.findAll(spec);


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
        return null;
    }

    @Override
    public GymMemberResponseDto initializeSubscription(Long memberId, GymMemberSubscriptionRequestDto requestDto) {
        return null;
    }


    private void initializeVisitBasedSubscription(Membership membership, GymMemberSubscriptionRequestDto requestDto) {
        LOGGER.info("Visit-based subscription detected. Initializing visits...");

        Integer allowedVisits = requestDto.getAllowedVisits() != null
                ? requestDto.getAllowedVisits()
                : SubscriptionPlan.VISIT_PASS.getDefaultVisits();

        membership
                .setAllowedVisits(allowedVisits)
                .setRemainingVisits(allowedVisits)
                .setSubscriptionStartDate(LocalDateTime.now())
                .setSubscriptionEndDate(null);
    }

    private void initializeTimeBasedSubscription(Membership membership) {
        LOGGER.info("Time-based subscription. Calculating expiry...");
        LocalDateTime now = LocalDateTime.now();
        membership
                .setSubscriptionStartDate(now)
                .setSubscriptionEndDate(calculateEndDate(now, membership.getSubscriptionPlan()))
                .setAllowedVisits(null)
                .setRemainingVisits(null);
    }


    private void recalculateSubscriptionStatus(Membership membership) {
        if (membership.getSubscriptionPlan() == null) {
            membership.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
            return;
        }

        if (membership.getSubscriptionPlan().isVisitBased()) {
            Integer remaining = membership.getRemainingVisits();
            if (remaining == null || remaining <= 0) {
                deactivateSubscription(membership);
            } else {
                membership.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            }
        }


    }

    private void validateSubscriptionChange(Membership membership, GymMemberUpdateRequestDto updateRequest) {
        SubscriptionPlan currentPlan = membership.getSubscriptionPlan();
        SubscriptionPlan newPlan = updateRequest.getSubscriptionPlan();

        if (currentPlan == newPlan) return;

        if (currentPlan != null && currentPlan.isTimeBased()) {
            if (membership.getSubscriptionEndDate() != null && LocalDateTime.now().isBefore(membership.getSubscriptionEndDate())) {
                throw new FitManageAppException(
                        "Cannot change time-based plan before current period ends.",
                        ApiErrorCode.UNAUTHORIZED
                );
            }
        }

        if (currentPlan != null && currentPlan.isVisitBased()) {
            if (membership.getRemainingVisits() != null && membership.getRemainingVisits() > 0) {
                throw new FitManageAppException(
                        "Cannot change visit-based plan until all visits are used.",
                        ApiErrorCode.UNAUTHORIZED
                );
            }
        }
    }


    private boolean handleVisitPass(Membership membership) {
        if (membership.getSubscriptionPlan() != SubscriptionPlan.VISIT_PASS) return true;

        Integer remaining = membership.getRemainingVisits();
        if (remaining == null || remaining <= 0) {
            deactivateSubscription(membership);
            return false;
        }

        membership.setRemainingVisits(membership.getRemainingVisits() - 1);
        recalculateSubscriptionStatus(membership);
        return true;
    }

    private void deactivateSubscription(Membership membership) {
        membership.setSubscriptionStatus(SubscriptionStatus.INACTIVE)
                .setSubscriptionPlan(null);
    }


    private Optional<User> findEntityBySmartQuery(String input, Long gymId) {
        return Optional.empty();
    }


    private <T> T mapToDto(Object source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }

    private User buildGymMember(Tenant tenant, UserCreateRequestDto requestDto) {
        User user = new User()
                .setUsername(requestDto.getEmail())
                .setEmail(requestDto.getEmail())
                .setGender(requestDto.getGender())
                .setBirthDate(requestDto.getBirthDate())
                .setPhone(requestDto.getPhone())
                .setTenant(tenant);

        LOGGER.info("Initial password for user with email: {} will be created", user.getEmail());
        user.setPassword(passwordEncoder.encode(generateDefaultPassword()))
                .setUpdatedAt(LocalDateTime.now());

        Role gymAdminRole = roleService.findByName(RoleType.MEMBER);
        user.getRoles().add(gymAdminRole);

        return user;
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

    private Tenant getTenantByEmail(String email) {
        return tenantRepository.findTenantByUserEmail(email)
                .orElseThrow(() -> {
                    LOGGER.warn("User with email {} not found.", email);
                    return new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
                });
    }

    private String getAuthenticatedGymEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        LOGGER.info("Authenticated user email: {}", email);
        return email;
    }

    private void validateCredentials(User user, UserCreateRequestDto requestDto) {
      
    }
}
