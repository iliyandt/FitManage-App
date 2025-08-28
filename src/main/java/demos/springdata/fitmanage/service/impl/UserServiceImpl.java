package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.users.*;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.UserRepository;
import demos.springdata.fitmanage.service.UserService;
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
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    public UserServiceImpl
            (UserRepository userRepository,
             ModelMapper modelMapper
            ) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }


    @Transactional
    @Override
    public UserProfileDto getUserProfileByEmail(String email) {
        LOGGER.info("Fetching gym with email: {}", email);
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));

        Set<RoleType> roles = extractRoleTypes(user);

        if (roles.contains(RoleType.FACILITY_MEMBER)) {
            return mapMemberProfile(user, roles);
        }

        if (roles.contains(RoleType.FACILITY_ADMIN) || roles.contains(RoleType.FACILITY_STAFF)) {
            return mapStaffProfile(user, roles);
        }

        return mapBaseProfile(user, roles);
    }

    @Override
    public UserProfileDto updateProfile(Long id, UserUpdateDto dto) {
        LOGGER.info("Updating basic info for user with id: {}", id);
        User user = findUserById(id);

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, user);
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        UserBaseResponseDto response = modelMapper.map(savedUser, UserBaseResponseDto.class);

        response.setRoles(extractRoleTypes(user));

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

    private UserBaseResponseDto mapBaseProfile(User user, Set<RoleType> roles) {
        UserBaseResponseDto dto = modelMapper.map(user, UserBaseResponseDto.class);
        dto.setBirthDate(user.getBirthDate());
        dto.setUsername(user.getActualUsername());
        dto.setRoles(roles);
        return dto;
    }

    private StaffResponseDto mapStaffProfile(User user, Set<RoleType> roles) {
        StaffResponseDto dto = modelMapper.map(user, StaffResponseDto.class);
        dto.setBirthDate(user.getBirthDate());
        dto.setUsername(user.getActualUsername());
        dto.setRoles(roles);
        dto.setMembersCount(user.getMemberships().size());
        return dto;
    }

    private MemberResponseDto mapMemberProfile(User user, Set<RoleType> roles) {
        MemberResponseDto dto = modelMapper.map(user, MemberResponseDto.class);
        dto.setBirthDate(user.getBirthDate());
        dto.setUsername(user.getActualUsername());
        dto.setRoles(roles);

        Membership membership = user.getMemberships().stream()
                .max(Comparator.comparing(Membership::getCreatedAt))
                .orElseThrow(() -> new IllegalStateException("User has no memberships"));

        modelMapper.map(membership, dto);
        return dto;
    }

    private Set<RoleType> extractRoleTypes(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

}
