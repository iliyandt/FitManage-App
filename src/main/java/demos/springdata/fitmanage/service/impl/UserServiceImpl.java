package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.tenant.TenantResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.MembershipRepository;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final MembershipRepository membershipRepository;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    public UserServiceImpl
            (UserRepository userRepository,
             TenantRepository tenantRepository,
             ModelMapper modelMapper,
             MembershipRepository membershipRepository
            ) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.modelMapper = modelMapper;
        this.membershipRepository = membershipRepository;
    }


    @Transactional(readOnly = true)
    @Override
    public TenantResponseDto getUserSummaryByEmail(String email) {
        LOGGER.info("Fetching gym with email: {}", email);
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));

        TenantResponseDto dto = modelMapper.map(user, TenantResponseDto.class);
        dto.setUsername(user.getActualUsername());

        int membersCount = user.getMemberships().size();
        dto.setMembersCount(membersCount);

        return dto;
    }


    @Transactional
    @Override
    public void updateUserProfile(String email, UserUpdateDto dto) {
        LOGGER.info("Updating basic info for gym with email: {}", email);
        User user = getUserOrElseThrow(email);

        User updatedUser = updateUserDetails(dto, user);

        userRepository.save(updatedUser);
        LOGGER.info("Updated basic info for user with email: {}", email);
    }

    @Override
    public boolean existsByEmailAndTenant(String email, Long tenantId) {
        return userRepository.existsByEmailAndTenant_Id(email, tenantId);
    }

    @Override
    public boolean existsByPhoneAndTenant(String phone, Long tenantId) {
        return userRepository.existsByPhoneAndTenant_Id(phone, tenantId);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public User getByIdAndTenantId(Long memberId, Long tenantId) {
        return userRepository.findByIdAndTenantId(memberId, tenantId)
                .orElseThrow(() -> new FitManageAppException("Gym member not found", ApiErrorCode.NOT_FOUND));
    }


    private User getUserOrElseThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.warn("User with email {} not found", email);
                    return new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
                });
    }

    private User updateUserDetails(UserUpdateDto dto, User user) {
        if (user.getUsername().equals(dto.getUsername())) {
            LOGGER.error("Username same as current one.");
            throw new FitManageAppException("Username cannot be the same.", ApiErrorCode.CONFLICT);
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            LOGGER.error("Username {} already taken.", dto.getUsername());
            throw new FitManageAppException("Username already taken.", ApiErrorCode.CONFLICT);
        }

        modelMapper.map(dto, user);
        LOGGER.info("User details for user with email: {} updated.", user.getEmail());
        return user;
    }




    private void initializeVisitBasedSubscription(Membership membership, MemberSubscriptionRequestDto requestDto) {
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

    private void validateSubscriptionChange(Membership membership, MemberUpdateRequestDto updateRequest) {
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



    private Tenant getTenantByEmail(String email) {
        return tenantRepository.findTenantByUserEmail(email)
                .orElseThrow(() -> {
                    LOGGER.warn("User with email {} not found.", email);
                    return new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
                });
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        LOGGER.info("Authenticated user email: {}", email);
        return email;
    }


}
