package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.MembershipRepository;
import demos.springdata.fitmanage.service.MembershipService;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.util.CurrentUserUtils;
import demos.springdata.fitmanage.util.RoleUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;


@Service
public class MembershipServiceImpl implements MembershipService {
    private final MembershipRepository membershipRepository;
    private final CurrentUserUtils currentUserUtils;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MembershipServiceImpl.class);
    private final UserService userService;

    @Autowired
    public MembershipServiceImpl(MembershipRepository membershipRepository, CurrentUserUtils currentUserUtils, ModelMapper modelMapper, UserService userService) {
        this.membershipRepository = membershipRepository;
        this.currentUserUtils = currentUserUtils;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    //TODO: when plan is single visit should it be automatically checkedIn or no? if yes the visit should be at the same day/hour, if not it can be used other time.
    @Transactional
    @Override
    public MemberResponseDto setupMembershipPlan(Long memberId, MemberSubscriptionRequestDto requestDto) {
        User user = userService.findUserById(memberId);
        Tenant tenant = user.getTenant();

        Membership membership = membershipRepository.findByUserAndTenant(user, tenant)
                .orElseThrow(() -> new FitManageAppException("User has no created membership.", ApiErrorCode.NOT_FOUND));

        validateSubscriptionChange(membership, requestDto);
        activateMembership(membership, user, requestDto);

        Membership savedMembership = membershipRepository.save(membership);
        user.getMemberships().add(savedMembership);

        return getMappedUserAndMembershipDetails(user, savedMembership);
    }

    @Override
    @Transactional
    public Membership checkIn(Membership membership) {
        if (membership.getSubscriptionPlan().isTimeBased()) {
            if (membership.getSubscriptionEndDate().isBefore(LocalDateTime.now())) {
                membership.setSubscriptionStatus(SubscriptionStatus.INACTIVE)
                        .setSubscriptionPlan(null)
                        .setSubscriptionStartDate(null)
                        .setSubscriptionEndDate(null);
                return membershipRepository.save(membership);
            }
        }

        if (membership.getSubscriptionPlan().isVisitBased()) {
            if (membership.getRemainingVisits() <= 0) {
                membership.setSubscriptionPlan(null);
                membership.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
                return membershipRepository.save(membership);
            }else {
                int remaining = membership.getRemainingVisits() - 1;
                membership.setRemainingVisits(remaining);
            }

        }

        return membershipRepository.save(membership);
    }

    @Override
    public Membership getRequiredActiveMembership(Set<Membership> memberships) {
        return memberships.stream()
                .filter(Membership::isActive)
                .findFirst()
                .orElseThrow(() -> new FitManageAppException("User doesn't have active membership", ApiErrorCode.NOT_FOUND));
    }

    @Override
    public void save(Membership membership) {
        membershipRepository.save(membership);
    }

    @Override
    public Double countByEmploymentForTenant(Employment employment) {
        Tenant tenant = currentUserUtils.getCurrentUser().getTenant();
        return membershipRepository.countByEmployment_AndTenant(employment, tenant);
    }

    @Override
    public Double countBySubscriptionStatusForTenant(SubscriptionStatus status) {
        Tenant tenant = currentUserUtils.getCurrentUser().getTenant();
        return membershipRepository.countBySubscriptionStatus_AndTenant(status, tenant);
    }

    @Override
    public Double countBySubscriptionPlanForTenant(SubscriptionPlan plan) {
        Tenant tenant = currentUserUtils.getCurrentUser().getTenant();


        return membershipRepository.countBySubscriptionPlan_AndTenant(plan, tenant);
    }

    private void initializeVisitBasedSubscription(Membership membership, MemberSubscriptionRequestDto requestDto) {
        LOGGER.info("Visit-based subscription detected. Initializing visits...");

        Integer allowedVisits = requestDto.getAllowedVisits() != null
                ? requestDto.getAllowedVisits()
                : SubscriptionPlan.VISIT_PASS.getDefaultVisits();

        membership
                .setSubscriptionPlan(requestDto.getSubscriptionPlan())
                .setSubscriptionStatus(SubscriptionStatus.ACTIVE)
                .setEmployment(requestDto.getEmployment())
                .setAllowedVisits(allowedVisits)
                .setRemainingVisits(allowedVisits)
                .setSubscriptionStartDate(LocalDateTime.now())
                .setSubscriptionEndDate(null);
    }

    private void initializeTimeBasedSubscription(Membership membership) {
        LOGGER.info("Time-based subscription. Calculating expiry...");
        LocalDateTime now = LocalDateTime.now();
        membership
                .setSubscriptionStatus(SubscriptionStatus.ACTIVE)
                .setSubscriptionStartDate(now)
                .setSubscriptionEndDate(calculateEndDate(now, membership.getSubscriptionPlan()));
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

    private MemberResponseDto getMappedUserAndMembershipDetails(User user, Membership savedMembership) {
        MemberResponseDto memberResponse = modelMapper.map(user, MemberResponseDto.class);
        modelMapper.map(savedMembership, memberResponse);
        memberResponse.setUsername(user.getActualUsername());
        memberResponse.setRoles(RoleUtils.extractRoleTypes(user));

        return memberResponse;
    }

    private void activateMembership(Membership membership, User user, MemberSubscriptionRequestDto requestDto) {
        LOGGER.info("Activating membership for user with username: {}", user.getActualUsername());
        SubscriptionPlan plan = requestDto.getSubscriptionPlan();

        if (plan.isVisitBased()) {
            initializeVisitBasedSubscription(membership, requestDto);
        } else {
            membership.setEmployment(requestDto.getEmployment());
            membership.setSubscriptionPlan(plan);
            initializeTimeBasedSubscription(membership);
        }

    }

    private void validateSubscriptionChange(Membership membership, MemberSubscriptionRequestDto updateRequest) {
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
}
