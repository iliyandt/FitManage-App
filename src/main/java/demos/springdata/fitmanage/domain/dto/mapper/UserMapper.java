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
    @BeanMapping(qualifiedByName = "toMember")
    @Mappings({
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "memberships", ignore = true),
            @Mapping(target = "enabled", constant = "true"),
            @Mapping(target = "passwordChanged", constant = "false"),
            @Mapping(target = "address", ignore = true),
            @Mapping(target = "city", ignore = true),
            @Mapping(target = "tenant", source = "tenant")
    })
    public abstract User toMember(Tenant tenant, CreateUser create);


    @Named("toAdmin")
    @BeanMapping(qualifiedByName = "toAdmin")
    @Mappings({
            @Mapping(target = "tenant", source = "tenant"),
            @Mapping(target = "address", ignore = true),
            @Mapping(target = "city", ignore = true),
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "enabled", constant = "false"),
            @Mapping(target = "passwordChanged", constant = "true"),
            @Mapping(target = "roles", ignore = true),
            @Mapping(target = "memberships", ignore = true),
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "verificationCode", ignore = true),
            @Mapping(target = "verificationCodeExpiresAt", ignore = true)
    })
    public abstract User toAdminUser(Tenant tenant, UserRegisterRequest request);


    @Named("toEmployee")
    @BeanMapping(qualifiedByName = "toMember")
    @Mappings({
            @Mapping(target = "tenant", source = "tenant"),
            @Mapping(target = "address", ignore = true),
            @Mapping(target = "city", ignore = true),
            @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "enabled", constant = "true"),
            @Mapping(target = "passwordChanged", constant = "false"),
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
    protected void setupEmployeeDetails(@MappingTarget User user) {
        Role role = roleService.findByName(RoleType.STAFF);
        user.setRoles(Set.of(role));
    }

    protected RoleType mapRole(Role role) {
        return RoleType.valueOf(role.getName().toString());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract User updateUserFields(UserUpdate update, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    public abstract void updateMembershipFields(UserUpdate source, @MappingTarget Membership target);

    @Mappings({
            @Mapping(source = "user.id", target = "id"),
            @Mapping(source = "user.roles", target = "roles"),
            @Mapping(source = "user.createdAt", target = "createdAt"),
            @Mapping(source = "membership.subscriptionPlan", target = "memberResponse.subscriptionPlan"),
            @Mapping(source = "membership.subscriptionStatus", target = "memberResponse.subscriptionStatus"),
            @Mapping(source = "membership.allowedVisits", target = "memberResponse.allowedVisits"),
            @Mapping(source = "membership.remainingVisits", target = "memberResponse.remainingVisits"),
            @Mapping(source = "membership.subscriptionStartDate", target = "memberResponse.subscriptionStartDate"),
            @Mapping(source = "membership.subscriptionEndDate", target = "memberResponse.subscriptionEndDate"),
            @Mapping(source = "membership.employment", target = "memberResponse.employment"),
            @Mapping(source = "membership.lastCheckInAt", target = "memberResponse.lastCheckInAt")
    })
    public abstract UserResponse toResponse(Membership membership, User user);

    @Mappings({
            @Mapping(source = "user.id", target = "id"),
            @Mapping(source = "user.roles", target = "roles"),
            @Mapping(source = "user.createdAt", target = "createdAt"),
            @Mapping(source = "employee.employeeRole", target = "employeeResponse.employeeRole")
    })
    public abstract UserResponse toResponse(Employee employee, User user);

    @Mappings({
            @Mapping(source = "user.id", target = "id"),
            @Mapping(source = "user.roles", target = "roles"),
            @Mapping(source = "user.createdAt", target = "createdAt")
    })
    public abstract UserResponse toResponse(User user);


}
