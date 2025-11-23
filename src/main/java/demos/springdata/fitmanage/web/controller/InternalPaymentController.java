package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.member.request.SubscriptionRequest;
import demos.springdata.fitmanage.domain.enums.Abonnement;
import demos.springdata.fitmanage.service.MembershipService;
import demos.springdata.fitmanage.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/payments")
// Този контролер трябва да е защитен. Не трябва да е достъпен от външния свят.
public class InternalPaymentController {

    private final TenantService tenantService;
    private final MembershipService membershipService;

    public InternalPaymentController(TenantService tenantService, MembershipService membershipService) {
        this.tenantService = tenantService;
        this.membershipService = membershipService;
    }

    @PostMapping("/tenants/{tenantId}/activate")
    public ResponseEntity<Void> activateTenantSubscription(@PathVariable String tenantId, @RequestParam("plan") String planName, @RequestParam("duration") String duration) {
        tenantService.createAbonnement(UUID.fromString(tenantId), Abonnement.valueOf(planName), duration);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/users/{userId}/memberships/activate")
    public ResponseEntity<Void> activateUserMembership(@PathVariable String userId, @RequestBody SubscriptionRequest request) {
        membershipService.setupMembershipPlan(UUID.fromString(userId), request);
        return ResponseEntity.ok().build();
    }
}
