package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.employee.EmployeeResponseDto;
import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.dto.users.*;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.Gender;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.util.CurrentUserUtils;
import demos.springdata.fitmanage.util.RoleUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CurrentUserUtils currentUserUtils;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    public UserServiceImpl
            (UserRepository userRepository,
             ModelMapper modelMapper,
             CurrentUserUtils currentUserUtils) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.currentUserUtils = currentUserUtils;
    }


    @Transactional
    @Override
    public UserResponseDto getUserProfileByEmail(String email) {
        LOGGER.info("Searching user with email: {}", email);
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));

        Set<RoleType> roles = RoleUtils.extractRoleTypes(user);

        return mapBaseProfile(user, roles);
    }


    @Override
    public UserResponseDto updateProfile(UserUpdateDto dto) {

        User currentlyLoggedUser = currentUserUtils.getCurrentUser();


        LOGGER.info("Updating basic info for user with id: {}", currentlyLoggedUser.getId());

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, currentlyLoggedUser);
        currentlyLoggedUser.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(currentlyLoggedUser);

        UserResponseDto response = modelMapper.map(savedUser, UserResponseDto.class);

        response.setRoles(RoleUtils.extractRoleTypes(currentlyLoggedUser));

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
        Tenant tenant = currentUserUtils.getCurrentUser().getTenant();
        List<User> users = userRepository.findByGender_AndTenant(gender, tenant);

        return users.stream()
                .filter(user -> {
                    Set<RoleType> roleTypes = RoleUtils.extractRoleTypes(user);
                    return roleTypes.contains(RoleType.FACILITY_MEMBER);
                })
                .count();
    }

    @Override
    public Long countAllUsersByTenant() {
        Tenant tenant = currentUserUtils.getCurrentUser().getTenant();
        return userRepository.countByTenantAndRoleType(tenant, RoleType.FACILITY_MEMBER);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));
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
        dto.setUsername(user.getActualUsername());
        dto.setRoles(roles);
        return dto;
    }

//    private EmployeeResponseDto mapStaffProfile(User user, Set<RoleType> roles, Employee employee) {
//        EmployeeResponseDto dto = modelMapper.map(user, EmployeeResponseDto.class);
//        dto.setBirthDate(user.getBirthDate());
//        dto.setUsername(user.getActualUsername());
//        dto.setRoles(roles);
//        dto.setMembersCount(user.getMemberships().size());
//        dto.setEmployeeRole(employee.getEmployeeRole());
//        return dto;
//    }
//
//    private MemberResponseDto mapMemberProfile(User user, Set<RoleType> roles) {
//        MemberResponseDto dto = modelMapper.map(user, MemberResponseDto.class);
//        dto.setBirthDate(user.getBirthDate());
//        dto.setUsername(user.getActualUsername());
//        dto.setRoles(roles);
//
//        Membership membership = user.getMemberships().stream()
//                .max(Comparator.comparing(Membership::getCreatedAt))
//                .orElseThrow(() -> new IllegalStateException("User has no memberships"));
//
//        modelMapper.map(membership, dto);
//        return dto;
//    }
}
