package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;

import java.util.UUID;

public interface AccessRequestService {
    UserResponse requestAccess(UUID tenantId, CreateUser createMemberRequest);
    UserResponse processAccessRequest(UUID membershipId, boolean approve);
}
