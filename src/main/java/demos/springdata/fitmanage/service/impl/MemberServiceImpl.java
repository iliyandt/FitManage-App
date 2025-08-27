package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.users.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.users.UserCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberTableDto;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.support.MemberSpecification;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.UserSecurityUtils;
import jakarta.mail.MessagingException;
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
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    private final UserService userService;
    private final RoleService roleService;
    private final TenantService tenantService;
    private final EmailService emailService;
    private final MembershipService membershipService;
    private final VisitService visitService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserSecurityUtils securityUtils;
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    public MemberServiceImpl
            (UserService userService,
             RoleService roleService,
             TenantService tenantService,
             EmailService emailService,
             ModelMapper modelMapper,
             BCryptPasswordEncoder passwordEncoder, UserSecurityUtils securityUtils, MembershipService membershipService, VisitService visitService) {
        this.userService = userService;
        this.roleService = roleService;
        this.tenantService = tenantService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
        this.membershipService = membershipService;
        this.visitService = visitService;
    }

    //TODO optimize every method where i should map the roles manually like this to use overall:
//    private Set<RoleType> extractRoleTypes(User user) {
//        return user.getRoles().stream()
//                .map(Role::getName)
//                .collect(Collectors.toSet());
//    }
    @Transactional
    @Override
    public UserProfileDto createMember(UserCreateRequestDto requestDto) {
        String userEmail = getAuthenticatedUserEmail();
        Tenant tenant = getTenantByEmail(userEmail);
        User user = buildAndSendVerificationEmail(requestDto, tenant);
        validateCredentials(user, requestDto);
        userService.save(user);
        LOGGER.info("Successfully added member with ID {} to facility '{}'", user.getId(), tenant.getName());

        return modelMapper.map(user, MemberResponseDto.class)
                .setRoles(
                        user.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                );
    }

    @Override
    public void removeMember(Long memberId) {
        String adminEmail = getAuthenticatedUserEmail();
        Tenant tenant = getTenantByEmail(adminEmail);

        User user = userService.getByIdAndTenantId(memberId, tenant.getId());

        LOGGER.info("Deleting member with ID {} from tenant {}", memberId, tenant.getName());
        userService.delete(user);
        LOGGER.info("Member with ID {} deleted successfully", memberId);
    }


    @Transactional
    @Override
    public UserProfileDto checkInMember(Long memberId) {
        User user = userService.findUserById(memberId);
        Membership activeMembership = membershipService.getRequiredActiveMembership(user.getMemberships());

        Membership updatedMembership = membershipService.checkIn(activeMembership);

        Visit visit = null;
        if (updatedMembership.getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
            visit = visitService.checkIn(updatedMembership, memberId);
        }

        return mapToResponseDto(user, updatedMembership, visit);
    }

    @Override
    public UserProfileDto updateMemberDetails(Long memberId, MemberUpdateDto updateRequest) {
        return userService.updateProfile(memberId, updateRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MemberTableDto> getAllMembersForTable() {
        LOGGER.info("Fetching all members..");
        String email = getAuthenticatedUserEmail();
        Tenant tenant = getTenantByEmail(email);

        Role facilityMemberRole = roleService.findByName(RoleType.FACILITY_MEMBER);


        return tenant.getUsers().stream()
                .filter(user -> user.getRoles().contains(facilityMemberRole))
                .map(this::mapUserToMemberTableDto)
                .toList();
    }

    //TODO: contains duplicate code from the getAllMembersForTable()
    @Transactional
    @Override
    public List<MemberTableDto> getMembersByFilter(MemberFilterRequestDto filter) {
        LOGGER.info("Search for users with filter: {}", filter);

        Tenant tenant = getTenantByEmail(getAuthenticatedUserEmail());

        Specification<User> spec = MemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("tenant"), tenant));

        List<User> memberList = userService.findMembersByFilter(spec);

        if (memberList.isEmpty())
            throw new FitManageAppException("No members found for the given filter", ApiErrorCode.NOT_FOUND);

        Role facilityMemberRole = roleService.findByName(RoleType.FACILITY_MEMBER);
        return memberList
                .stream()
                .filter(user -> user.getRoles().contains(facilityMemberRole))
                .map(user -> {
                    MemberTableDto dto = modelMapper.map(user, MemberTableDto.class);
                    //TODO: when no subscription inactive status + employment and plan?
                    Optional<Membership> activeMembership = membershipService.getActiveMembership(user.getMemberships());

                    modelMapper.map(activeMembership, dto);

                    Set<RoleType> roleTypes = user.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet());
                    dto.setRoles(roleTypes);

                    return dto;
                })
                .toList();
    }


    //TODO: refactor, it contains duplicate code lines form UserServiceImpl: mapMemberProfile()
    @Transactional
    @Override
    public UserProfileDto findMember(MemberFilterRequestDto filter) {
        User user = findFirstMemberByFilter(filter)
                .orElseThrow(() -> new FitManageAppException("Member not found", ApiErrorCode.NOT_FOUND));

        MemberResponseDto dto = modelMapper.map(user, MemberResponseDto.class);
        dto.setUsername(user.getActualUsername());

        Set<RoleType> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        dto.setRoles(roles);

        user.getMemberships().stream().findFirst().ifPresent(m -> {
            dto.setSubscriptionPlan(m.getSubscriptionPlan())
                    .setSubscriptionStatus(m.getSubscriptionStatus())
                    .setSubscriptionStartDate(m.getSubscriptionStartDate())
                    .setSubscriptionEndDate(m.getSubscriptionEndDate())
                    .setAllowedVisits(m.getAllowedVisits())
                    .setRemainingVisits(m.getRemainingVisits())
                    .setLastCheckInAt(m.getLastCheckInAt())
                    .setEmployment(m.getEmployment());
        });

        return dto;
    }


    public Tenant getTenantByEmail(String email) {
        return tenantService.getTenantByEmail(email);
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        LOGGER.info("Authenticated user email: {}", email);
        return email;
    }

    //TODO: refactor for better separation of concerns
    private User buildMember(Tenant tenant, UserCreateRequestDto requestDto) throws MessagingException {
        User user = new User()
                .setFirstName(requestDto.getFirstName())
                .setLastName(requestDto.getLastName())
                .setUsername(requestDto.getUsername())
                .setEmail(requestDto.getEmail())
                .setGender(requestDto.getGender())
                .setBirthDate(requestDto.getBirthDate())
                .setPhone(requestDto.getPhone())
                .setTenant(tenant)
                .setCreatedAt(LocalDateTime.now())
                .setVerificationCode(securityUtils.generateVerificationCode())
                .setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));



        LOGGER.info("Initial password for user with email: {} will be created", user.getEmail());
        String initialPassword = securityUtils.generateDefaultPassword();

        emailService.sendUserVerificationEmail(user.getEmail(), "Account verification", "Verification code: " + user.getVerificationCode());


        sendInitialPassword(user, initialPassword);

        user.setPassword(passwordEncoder.encode(initialPassword))
                .setUpdatedAt(LocalDateTime.now());

        Role role = roleService.findByName(RoleType.FACILITY_MEMBER);
        user.getRoles().add(role);

        return user;
    }

    private void validateCredentials(User member, UserCreateRequestDto requestDto) {
        Map<String, String> errors = new HashMap<>();

        if (userService.existsByEmailAndTenant(requestDto.getEmail(), member.getTenant().getId())) {
            LOGGER.warn("Member with email {} already exists", requestDto.getEmail());
            errors.put("email", "Email is already registered");
        }

        if (userService.existsByPhoneAndTenant(requestDto.getPhone(), member.getTenant().getId())) {
            LOGGER.warn("Member with phone {} already exists", member.getPhone());
            errors.put("phone", "Phone used from another member");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }

    //TODO: extract htmlMessage code, Update with company logo
    private void sendInitialPassword(User user, String initialPassword) {
        String subject = "Password";
        String password = "PASSWORD " + initialPassword;
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + password + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            LOGGER.info("Sending initial password to: {}", user.getEmail());
            emailService.sendUserVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send password to: {}", user.getEmail(), e);
            throw new FitManageAppException("Failed to send password to user", ApiErrorCode.INTERNAL_ERROR);
        }
    }

    //TODO: what happens when searching by first and last name and there are 2 members with identical names?
    private Optional<User> findFirstMemberByFilter(MemberFilterRequestDto filter) {
        Tenant tenant = getTenantByEmail(getAuthenticatedUserEmail());

        Specification<User> spec = MemberSpecification.build(filter)
                .and((root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenant.getId()));

        return userService.findFirstMemberByFilter(spec);
    }

    private User buildAndSendVerificationEmail(UserCreateRequestDto requestDto, Tenant tenant) {
        User user;
        try {
            user = buildMember(tenant, requestDto);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send verification email while creating member: {}", requestDto.getEmail(), e);
            throw new FitManageAppException("Failed to send verification email", ApiErrorCode.INTERNAL_ERROR);
        }
        return user;
    }

    private MemberResponseDto mapToResponseDto(User user, Membership updatedMembership, Visit visit) {
        MemberResponseDto response = modelMapper.map(user, MemberResponseDto.class)
                .setAllowedVisits(updatedMembership.getAllowedVisits())
                .setRemainingVisits(updatedMembership.getRemainingVisits())
                .setEmployment(updatedMembership.getEmployment())
                .setSubscriptionPlan(updatedMembership.getSubscriptionPlan())
                .setSubscriptionStatus(updatedMembership.getSubscriptionStatus())
                .setSubscriptionStartDate(updatedMembership.getSubscriptionStartDate())
                .setSubscriptionEndDate(updatedMembership.getSubscriptionEndDate());

        if (visit != null) {
            response.setLastCheckInAt(visit.getCheckInAt());
        }

        return response;
    }

    private MemberTableDto mapUserToMemberTableDto(User user) {
        MemberTableDto dto = modelMapper.map(user, MemberTableDto.class);

        membershipService.getActiveMembership(user.getMemberships())
                .ifPresent(membership -> {
                    dto.setSubscriptionStatus(membership.getSubscriptionStatus());
                    dto.setSubscriptionPlan(membership.getSubscriptionPlan());
                    dto.setAllowedVisits(membership.getAllowedVisits());
                    dto.setRemainingVisits(membership.getRemainingVisits());
                    dto.setEmployment(membership.getEmployment());
                });

        Set<RoleType> roleTypes = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        dto.setRoles(roleTypes);

        return dto;
    }
}
