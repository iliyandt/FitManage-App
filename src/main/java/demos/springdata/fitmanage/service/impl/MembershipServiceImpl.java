package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberUpdateDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserResponseDto;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.MembershipRepository;
import demos.springdata.fitmanage.service.MembershipService;
import demos.springdata.fitmanage.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;


@Service
public class MembershipServiceImpl implements MembershipService {
    private final MembershipRepository membershipRepository;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(MembershipServiceImpl.class);
    private final UserService userService;

    @Autowired
    public MembershipServiceImpl(MembershipRepository membershipRepository, ModelMapper modelMapper, UserService userService) {
        this.membershipRepository = membershipRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }


    //TODO: response returns only the membership details but not the user details
    @Transactional
    @Override
    public UserResponseDto initializeSubscription(Long memberId, MemberSubscriptionRequestDto requestDto) {
        User user = userService.findUserById(memberId);
        Tenant tenant = user.getTenant();

        LOGGER.info("Initializing subscription for user with username: {}", user.getActualUsername());

        Membership membership = membershipRepository.findByUserAndTenant(user, tenant)
                .orElseGet(() -> new Membership()
                        .setUser(user)
                        .setTenant(tenant));

        SubscriptionPlan plan = requestDto.getSubscriptionPlan();
        membership.setSubscriptionPlan(plan)
                .setEmployment(requestDto.getEmployment());

        if (plan.isVisitBased()) {
            initializeVisitBasedSubscription(membership, requestDto);
        } else {
            initializeTimeBasedSubscription(membership);
        }

        Membership saved = membershipRepository.save(membership);

        return modelMapper.map(saved, UserResponseDto.class);
    }


    @Override
    @Transactional
    public Membership checkIn(Membership membership) {
        if (membership.getSubscriptionPlan().isVisitBased()) {
            int remaining = membership.getRemainingVisits() - 1;
            membership.setRemainingVisits(Math.max(remaining, 0));

            if (remaining <= 0) {
                membership.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
            }
        }
        return membershipRepository.save(membership);
    }

    @Override
    public Membership getActiveMembership(Set<Membership> memberships) {
        return memberships.stream()
                .filter(Membership::isActive)
                .findFirst()
                .orElseThrow(() -> new FitManageAppException("User doesn't have active membership", ApiErrorCode.NOT_FOUND));
    }

    private void initializeVisitBasedSubscription(Membership membership, MemberSubscriptionRequestDto requestDto) {
        LOGGER.info("Visit-based subscription detected. Initializing visits...");

        Integer allowedVisits = requestDto.getAllowedVisits() != null
                ? requestDto.getAllowedVisits()
                : SubscriptionPlan.VISIT_PASS.getDefaultVisits();

        membership
                .setAllowedVisits(allowedVisits)
                .setRemainingVisits(allowedVisits)
                .setSubscriptionStatus(SubscriptionStatus.ACTIVE)
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

    private void validateSubscriptionChange(Membership membership, MemberUpdateDto updateRequest) {
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
