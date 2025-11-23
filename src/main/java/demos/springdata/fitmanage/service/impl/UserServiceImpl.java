package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.mapper.UserMapper;
import demos.springdata.fitmanage.domain.dto.users.*;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final RoleService roleService;
    private final UserMapper userMapper;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userMapper = userMapper;
    }

    @Override
    public User getCurrentUser() {
        UserData user = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(user.getId()).orElseThrow(() -> new DamilSoftException("User not found", HttpStatus.NOT_FOUND));
    }

    //TODO: passwordChanged should be true if user is admin
    @Transactional
    @Override
    public UserResponse getUserProfileByEmail(String email) {
        LOGGER.info("Searching user with email: {}", email);
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new DamilSoftException("User not found", HttpStatus.NOT_FOUND));

        if (UserRoleHelper.extractRoleTypes(user).contains(RoleType.MEMBER)) {
            Membership membership = user.getMemberships().stream().findFirst().orElseThrow(() -> new DamilSoftException("User has no membership", HttpStatus.INTERNAL_SERVER_ERROR));
            return userMapper.toResponse(membership, user);
        } else if (UserRoleHelper.extractRoleTypes(user).contains(RoleType.STAFF)) {
            Employee employee = user.getEmployees().stream().findFirst().orElseThrow(() -> new DamilSoftException("Employee not found.", HttpStatus.INTERNAL_SERVER_ERROR));
            return userMapper.toResponse(employee, user);
        } else {
            return userMapper.toResponse(user);
        }
    }

    @Override
    public UserResponse updateProfile(UserUpdate update) {
        User user = this.getCurrentUser();
        LOGGER.info("Updating basic info for user with id: {}", user.getId());
        User savedUser = userRepository.save(userMapper.updateUserFields(update, user));
        return userMapper.toResponse(savedUser);
    }

    @Override
    public boolean existsByEmailAndTenant(String email, UUID tenantId) {
        return userRepository.existsByEmailAndTenant_Id(email, tenantId);
    }

    @Override
    public boolean existsByPhoneAndTenant(String phone, UUID tenantId) {
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
    public Long countByGenderForTenant(Gender gender) {
        Tenant tenant = this.getCurrentUser().getTenant();
        List<User> users = userRepository.findByGender_AndTenant(gender, tenant);

        return users.stream()
                .filter(user -> {
                    Set<RoleType> roleTypes = UserRoleHelper.extractRoleTypes(user);
                    return roleTypes.contains(RoleType.MEMBER);
                })
                .count();
    }

    @Override
    public Long countAllUsersByTenant() {
        Tenant tenant = this.getCurrentUser().getTenant();
        return userRepository.countByTenantAndRoleType(tenant, RoleType.MEMBER);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new DamilSoftException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<User> findUsersWithRoles(Set<String> roleNames) {
        Set<RoleType> roleTypes = roleNames.stream()
                .map(RoleType::valueOf)
                .collect(Collectors.toSet());

        Set<Role> roles = roleService.findByNameIn(roleTypes);

        UUID tenantId = this.getCurrentUser().getTenant().getId();

        return userRepository.findUsersByRolesAndTenant(roles, tenantId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getByIdAndTenantId(UUID memberId, UUID tenantId) {
        return userRepository.findByIdAndTenantId(memberId, tenantId).orElseThrow(() -> new DamilSoftException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<User> findMembersByFilter(Specification<User> spec) {
        return userRepository.findAll(spec);
    }

    @Override
    public List<User> findFirstMemberByFilter(Specification<User> spec) {
        return userRepository.findAll(spec);
    }

    @Override
    public User findUserById(UUID memberId) {
        return userRepository.findById(memberId).orElseThrow(() -> new DamilSoftException("User not found", HttpStatus.NOT_FOUND));
    }
}
