package demos.springdata.fitmanage.domain.dto.mapper;

import demos.springdata.fitmanage.domain.dto.auth.request.UserRegisterRequest;
import demos.springdata.fitmanage.domain.dto.users.CreateUser;
import demos.springdata.fitmanage.domain.dto.users.UserResponse;
import demos.springdata.fitmanage.domain.dto.users.UserUpdate;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.util.SecurityCodeGenerator;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public abstract class UserMapper {

    @Autowired
    private  RoleService roleService;
    @Autowired
    protected PasswordEncoder passwordEncoder;


    @Named("toMember")
    @Mappings({
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "memberships", ignore = true),
            @Mapping(target = "enabled", constant = "true"),
            @Mapping(target = "address", ignore = true),
            @Mapping(target = "city", ignore = true),
            @Mapping(target = "tenant", source = "tenant")
    })
    public abstract User toMember(Tenant tenant, CreateUser create);


    @Named("toAdmin")
    @Mappings({
            @Mapping(target = "tenant", source = "tenant"),
            @Mapping(target = "address", ignore = true),
            @Mapping(target = "city", ignore = true),
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "enabled", constant = "false"),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "memberships", ignore = true),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "verificationCode", ignore = true),
            @Mapping(target = "verificationCodeExpiresAt", ignore = true)
    })
    public abstract User toAdminUser(Tenant tenant, UserRegisterRequest request);


    @Named("toEmployee")
    @Mappings({
            @Mapping(target = "tenant", source = "tenant"),
            @Mapping(target = "address", ignore = true),
            @Mapping(target = "city", ignore = true),
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "enabled", constant = "true"),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "memberships", ignore = true)
    })
    public abstract User toEmployeeUser(Tenant tenant, CreateUser request);


    @AfterMapping
    @Named("toMember")
    protected void setupMemberRoles(@MappingTarget User user) {
        user.setRoles(Set.of(roleService.findByName(RoleType.MEMBER)));
    }

    @AfterMapping
    @Named("toAdmin")
    protected void setupAdminDetails(@MappingTarget User user, UserRegisterRequest request) {
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(roleService.findByName(RoleType.ADMIN)));
        user.setVerificationCode(SecurityCodeGenerator.generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
    }

    @AfterMapping
    @Named("toEmployee")
    protected void setupEmployeeDetails(@MappingTarget User user, CreateUser request) {
        user.setRoles(Set.of(roleService.findByName(RoleType.STAFF)));
    }

    protected RoleType mapRole(Role role) {
        return RoleType.valueOf(role.getName().toString());
    }

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

    @Mappings({
            @Mapping(source = "user.id", target = "id"),
            @Mapping(source = "user.roles", target = "roles"),
            @Mapping(source = "user.createdAt", target = "createdAt"),
            @Mapping(source = "employee.employeeRole", target = "employeeDetails.employeeRole")
    })
    public abstract UserResponse toResponse(Employee employee, User user);



}
