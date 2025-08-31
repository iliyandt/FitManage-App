package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.employee.EmployeeResponseDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeTableDto;
import demos.springdata.fitmanage.domain.dto.employee.EmployeeCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.users.*;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.EmployeeRepository;
import demos.springdata.fitmanage.service.*;
import demos.springdata.fitmanage.util.UserSecurityUtils;
import jakarta.mail.MessagingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final TenantService tenantService;
    private final RoleService roleService;
    private final UserService userService;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final UserSecurityUtils securityUtils;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, TenantService tenantService, RoleService roleService, UserService userService, EmailService emailService, ModelMapper modelMapper, UserSecurityUtils securityUtils, BCryptPasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.tenantService = tenantService;
        this.roleService = roleService;
        this.userService = userService;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.securityUtils = securityUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public UserProfileDto createEmployee(EmployeeCreateRequestDto requestDto) {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Tenant tenant = tenantService.getTenantByEmail(authenticatedUserEmail);

        User user = buildEmployee(tenant, requestDto);
        validateCredentials(user, requestDto);
        createAndSendInitialPasswordToUser(user);

        Employee employee = createAndLinkStaffProfileToUser(tenant, user, requestDto);

        userService.save(user);
        employeeRepository.save(employee);

        LOGGER.info("Successfully added staff with ID {} to facility '{}'", user.getId(), tenant.getName());

        EmployeeResponseDto mappedStaff = modelMapper.map(user, EmployeeResponseDto.class);
        mappedStaff.setRoles(extractRoleTypes(user));
        modelMapper.map(employee, mappedStaff);
        mappedStaff.setEmployeeRole(requestDto.getEmployeeRole());

        return mappedStaff;
    }

    @Transactional
    @Override
    public List<EmployeeTableDto> getAllEmployees() {
        LOGGER.info("Fetching all members..");
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Tenant tenant = tenantService.getTenantByEmail(authenticatedUserEmail);
        LOGGER.info("Tenant ID: {}", tenant.getId());

        List<Employee> employees = employeeRepository.findAllByTenant_Id(tenant.getId());
        LOGGER.info("Found {} employees", employees.size());

        return employees.stream()
                .map(this::mapEmployeeToEmployeeTableDto)
                .toList();
    }

    private Employee createAndLinkStaffProfileToUser(Tenant tenant, User user, EmployeeCreateRequestDto requestDto) {
        Employee employee = new Employee()
                .setTenant(tenant)
                .setUser(user)
                .setEmployeeRole(requestDto.getEmployeeRole());

        user.getEmployees().add(employee);
        return employee;
    }

    private User buildEmployee(Tenant tenant, UserCreateRequestDto requestDto) {

        User user = modelMapper.map(requestDto, User.class);
        user.setTenant(tenant)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setEnabled(true);

        Role role = roleService.findByName(RoleType.FACILITY_STAFF);
        user.getRoles().add(role);

        return user;
    }

    //TODO: override method for validation because now it duplicates same logic in every service
    private void validateCredentials(User member, UserCreateRequestDto requestDto) {
        Map<String, String> errors = new HashMap<>();

        if (userService.existsByEmailAndTenant(requestDto.getEmail(), member.getTenant().getId())) {
            LOGGER.warn("User with email {} already exists", requestDto.getEmail());
            errors.put("email", "Email is already registered");
        }

        if (userService.existsByPhoneAndTenant(requestDto.getPhone(), member.getTenant().getId())) {
            LOGGER.warn("User with phone {} already exists", member.getPhone());
            errors.put("phone", "Phone used from another member");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }

    private void createAndSendInitialPasswordToUser(User user) {
        LOGGER.info("Initial password for user with email: {} will be created", user.getEmail());
        String initialPassword = securityUtils.generateDefaultPassword();
        sendInitialPassword(user, initialPassword);
        user.setPassword(passwordEncoder.encode(initialPassword))
                .setUpdatedAt(LocalDateTime.now());
    }

    //TODO: extract method so it does not duplicate same logic in every service, extract htmlMessage code, Update with company logo
    private void sendInitialPassword(User user, String initialPassword) {
        String subject = "Password";
        String password = "PASSWORD " + initialPassword;
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + password + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            LOGGER.info("Sending initial password to: {}", user.getEmail());
            emailService.sendUserVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send password to: {}", user.getEmail(), e);
            throw new FitManageAppException("Failed to send password to user", ApiErrorCode.INTERNAL_ERROR);
        }
    }

    private Set<RoleType> extractRoleTypes(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private EmployeeTableDto mapEmployeeToEmployeeTableDto(Employee employee) {
        User user = employee.getUser();
        EmployeeTableDto dto = modelMapper.map(user, EmployeeTableDto.class);
        dto.setRoles(extractRoleTypes(user));
        dto.setEmployeeRole(employee.getEmployeeRole());
        return dto;
    }
}
