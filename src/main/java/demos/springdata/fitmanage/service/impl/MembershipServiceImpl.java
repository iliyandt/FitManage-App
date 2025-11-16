package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.member.response.MemberResponse;
import demos.springdata.fitmanage.domain.dto.member.request.SubscriptionRequest;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.Employment;
import demos.springdata.fitmanage.domain.enums.SubscriptionPlan;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.MembershipRepository;
import demos.springdata.fitmanage.service.MembershipService;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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


    @Transactional
    @Override
    public MemberResponse setupMembershipPlan(Long memberId, SubscriptionRequest requestDto) {
        User user = userService.findUserById(memberId);
        Tenant tenant = user.getTenant();

        Membership membership = membershipRepository.findByUserAndTenant(user, tenant)
                .orElseThrow(() -> new DamilSoftException("User has no created membership.", HttpStatus.NOT_FOUND));

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
            if (membership.getSubscriptionEndDate().isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS))) {
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
                .orElseThrow(() -> new DamilSoftException("User doesn't have active membership", HttpStatus.NOT_FOUND));
    }

    @Override
    public void save(Membership membership) {
        membershipRepository.save(membership);
    }

    @Override
    public Double countByEmploymentForTenant(Employment employment) {
        Tenant tenant = userService.getCurrentUser().getTenant();
        return membershipRepository.countByEmployment_AndTenant(employment, tenant);
    }

    @Override
    public Double countBySubscriptionStatusForTenant(SubscriptionStatus status) {
        Tenant tenant = userService.getCurrentUser().getTenant();
        return membershipRepository.countBySubscriptionStatus_AndTenant(status, tenant);
    }

    @Override
    public Double countBySubscriptionPlanForTenant(SubscriptionPlan plan) {
        Tenant tenant = userService.getCurrentUser().getTenant();
        return membershipRepository.countBySubscriptionPlan_AndTenant(plan, tenant);
    }

    @Override
    public Optional<Membership> getMembershipById(Long membershipId) {
        return membershipRepository.getMembershipById(membershipId);
    }

    private void initializeVisitBasedSubscription(Membership membership, SubscriptionRequest requestDto) {
        LOGGER.info("Visit-based subscription detected. Initializing visits...");

        Integer allowedVisits = requestDto.allowedVisits() != null
                ? requestDto.allowedVisits()
                : SubscriptionPlan.VISIT_PASS.getDefaultVisits();

        membership
                .setSubscriptionPlan(requestDto.subscriptionPlan())
                .setSubscriptionStatus(SubscriptionStatus.ACTIVE)
                .setEmployment(requestDto.employment())
                .setAllowedVisits(allowedVisits)
                .setRemainingVisits(allowedVisits)
                .setSubscriptionStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS))
                .setSubscriptionEndDate(null);
    }

    private void initializeTimeBasedSubscription(Membership membership) {
        LOGGER.info("Time-based subscription. Calculating expiry...");
        LocalDateTime now = LocalDateTime.now();
        membership
                .setSubscriptionStatus(SubscriptionStatus.ACTIVE)
                .setSubscriptionStartDate(now.truncatedTo(ChronoUnit.DAYS))
                .setSubscriptionEndDate(calculateEndDate(now.truncatedTo(ChronoUnit.DAYS), membership.getSubscriptionPlan()));
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

    private MemberResponse getMappedUserAndMembershipDetails(User user, Membership savedMembership) {
        MemberResponse memberResponse = modelMapper.map(user, MemberResponse.class);
        modelMapper.map(savedMembership, memberResponse);
        memberResponse.setUsername(user.getUsername());
        memberResponse.setRoles(UserRoleHelper.extractRoleTypes(user));

        return memberResponse;
    }

    private void activateMembership(Membership membership, User user, SubscriptionRequest requestDto) {
        LOGGER.info("Activating membership for user with username: {}", user.getUsername());
        SubscriptionPlan plan = requestDto.subscriptionPlan();

        if (plan.isVisitBased()) {
            initializeVisitBasedSubscription(membership, requestDto);
        } else {
            membership.setEmployment(requestDto.employment());
            membership.setSubscriptionPlan(plan);
            initializeTimeBasedSubscription(membership);
        }

    }

    private void validateSubscriptionChange(Membership membership, SubscriptionRequest updateRequest) {
        SubscriptionPlan currentPlan = membership.getSubscriptionPlan();
        SubscriptionPlan newPlan = updateRequest.subscriptionPlan();

        if (currentPlan == newPlan) return;

        if (currentPlan != null && currentPlan.isTimeBased()) {
            LocalDateTime now = LocalDateTime.now();
            if (membership.getSubscriptionEndDate() != null && now.truncatedTo(ChronoUnit.DAYS).isBefore(membership.getSubscriptionEndDate())) {
                throw new DamilSoftException(
                        "Cannot change time-based plan before current period ends.",
                        HttpStatus.UNAUTHORIZED
                );
            }
        }

        if (currentPlan != null && currentPlan.isVisitBased()) {
            if (membership.getRemainingVisits() != null && membership.getRemainingVisits() > 0) {
                throw new DamilSoftException(
                        "Cannot change visit-based plan until all visits are used.",
                        HttpStatus.UNAUTHORIZED
                );
            }
        }
    }
}
