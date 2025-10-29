package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.users.*;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.security.UserData;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    public UserServiceImpl
            (
                    UserRepository userRepository,
                    ModelMapper modelMapper
            ) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public User getCurrentUser() {
        UserData user = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(user.getId()).orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));
    }




    @Transactional
    @Override
    public UserResponseDto getUserProfileByEmail(String email) {
        LOGGER.info("Searching user with email: {}", email);
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));

        Set<RoleType> roles = UserRoleHelper.extractRoleTypes(user);

        return mapBaseProfile(user, roles);
    }


    @Override
    public UserResponseDto updateProfile(UserUpdateDto dto) {

        User currentlyLoggedUser = this.getCurrentUser();


        LOGGER.info("Updating basic info for user with id: {}", currentlyLoggedUser.getId());

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, currentlyLoggedUser);
        currentlyLoggedUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(currentlyLoggedUser);

        UserResponseDto response = modelMapper.map(savedUser, UserResponseDto.class);

        response.setRoles(UserRoleHelper.extractRoleTypes(currentlyLoggedUser));

        return response;
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
        return userRepository.findByEmail(email).orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));
    }

    @Override
    public Set<User> findAllUsersByIdsOrRoles(Set<Long> ids, Set<RoleType> targetRoles, Long tenantId) {
        return userRepository.findAllByIdsOrRoleTypesAndTenant(ids, targetRoles, tenantId);
    }


    @Override
    public User getByIdAndTenantId(Long memberId, Long tenantId) {
        return userRepository.findByIdAndTenantId(memberId, tenantId)
                .orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));
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
    public User findUserById(Long memberId) {
        return userRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));
    }


    @Override
    public Optional<User> findByQrToken(String qrToken) {
        return userRepository.findByQrToken(qrToken);
    }

    private UserResponseDto mapBaseProfile(User user, Set<RoleType> roles) {
        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        dto.setBirthDate(user.getBirthDate());
        dto.setUsername(user.getUsername());
        dto.setRoles(roles);
        return dto;
    }
}
