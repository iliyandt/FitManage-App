package demos.springdata.fitmanage.domain.dto.mapper;

import demos.springdata.fitmanage.domain.dto.member.request.MemberUpdate;
import demos.springdata.fitmanage.domain.dto.member.response.MemberDetails;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.dto.users.UserUpdate;
import demos.springdata.fitmanage.domain.entity.Membership;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.service.RoleService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public abstract class MemberMapper {

    @Autowired
    private  RoleService roleService;


    @Mappings({
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "memberships", ignore = true),
            @Mapping(target = "enabled", constant = "true"),
            @Mapping(target = "address", ignore = true),
            @Mapping(target = "city", ignore = true)
    })
    public abstract User toUser(Tenant tenant, CreateUser create);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUserFields(UserUpdate update, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    public abstract void updateMembershipFields(UserUpdate source, @MappingTarget Membership target);

    @Mappings({
            @Mapping(source = "user.id", target = "id"),
            @Mapping(source = "user.roles", target = "roles"),
            @Mapping(source = "user.createdAt", target = "createdAt"),
            @Mapping(source = "membership.subscriptionPlan", target = "memberDetails.subscriptionPlan"),
            @Mapping(source = "membership.subscriptionStatus", target = "memberDetails.subscriptionStatus"),
            @Mapping(source = "membership.allowedVisits", target = "memberDetails.allowedVisits"),
            @Mapping(source = "membership.remainingVisits", target = "memberDetails.remainingVisits"),
            @Mapping(source = "membership.subscriptionStartDate", target = "memberDetails.subscriptionStartDate"),
            @Mapping(source = "membership.subscriptionEndDate", target = "memberDetails.subscriptionEndDate"),
            @Mapping(source = "membership.employment", target = "memberDetails.employment"),
            @Mapping(source = "membership.lastCheckInAt", target = "memberDetails.lastCheckInAt")
    })
    public abstract UserResponse toResponse(Membership membership, User user);



    @AfterMapping
    protected void setupRoles(@MappingTarget User user) {
        user.setRoles(Set.of(roleService.findByName(RoleType.MEMBER)));
    }


    protected RoleType mapRole(Role role) {
        return RoleType.valueOf(role.getName().toString());
    }

}
