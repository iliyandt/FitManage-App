package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.tenant.TenantResponseDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.UserUpdateDto;
import demos.springdata.fitmanage.domain.entity.*;
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
import java.util.List;
import java.util.Optional;

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


    @Transactional(readOnly = true)
    @Override
    public TenantResponseDto getUserSummaryByEmail(String email) {
        LOGGER.info("Fetching gym with email: {}", email);
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));

        TenantResponseDto dto = modelMapper.map(user, TenantResponseDto.class);
        dto.setUsername(user.getActualUsername());

        int membersCount = user.getMemberships().size();
        dto.setMembersCount(membersCount);

        return dto;
    }

    @Override
    public void updateUserProfile(String email, UserUpdateDto dto) {
        LOGGER.info("Updating basic info for gym with email: {}", email);
        User user = getUserOrElseThrow(email);
        modelMapper.map(dto, user);
        userRepository.save(user);
        LOGGER.info("Updated basic info for user with email: {}", email);
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
                .orElseThrow(() -> new FitManageAppException("Мember not found", ApiErrorCode.NOT_FOUND));
    }

    @Override
    public List<User> findMembersByFilter(Specification<User> spec) {
        return userRepository.findAll(spec);
    }

    @Override
    public Optional<User> findFirstMemberByFilter(Specification<User> spec) {
        return userRepository.findOne(spec);
    }

    @Override
    public User findMemberById(Long memberId) {
        return userRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND));
    }

    @Override
    public User getUserOrElseThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOGGER.warn("User with email {} not found", email);
                    return new FitManageAppException("User not found", ApiErrorCode.NOT_FOUND);
                });
    }
}
