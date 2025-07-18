package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.team.RoleOptionDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberRequestDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.team.StaffMemberTableDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.PredefinedStaffRole;
import demos.springdata.fitmanage.domain.entity.StaffMember;
import demos.springdata.fitmanage.domain.entity.StaffRole;
import demos.springdata.fitmanage.domain.enums.StaffPosition;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.PredefinedStaffRoleRepository;
import demos.springdata.fitmanage.repository.StaffMemberRepository;
import demos.springdata.fitmanage.repository.StaffRoleRepository;
import demos.springdata.fitmanage.service.StaffMemberService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffMemberServiceImpl implements StaffMemberService {

    private final GymRepository gymRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PredefinedStaffRoleRepository predefinedStaffRoleRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public StaffMemberServiceImpl(GymRepository gymRepository, StaffRoleRepository staffRoleRepository, StaffMemberRepository staffMemberRepository, BCryptPasswordEncoder passwordEncoder, PredefinedStaffRoleRepository predefinedStaffRoleRepository, ModelMapper modelMapper) {
        this.gymRepository = gymRepository;
        this.staffRoleRepository = staffRoleRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.predefinedStaffRoleRepository = predefinedStaffRoleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<StaffMemberTableDto> getStaffMembersTableData() {
        String gymEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Gym gym = gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));


        return staffMemberRepository.findAllByGym(gym).stream()
                .map(staff -> modelMapper.map(staff, StaffMemberTableDto.class))
                .toList();
    }

    @Override
    @Transactional
    public List<StaffMemberResponseDto> createStaffMembers(List<StaffMemberRequestDto> requests, String gymEmail) {
        Gym gym = gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));
        return requests.stream()
                .map(request -> createSingleStaffMember(request, gym))
                .toList();
    }

    @Override
    public List<RoleOptionDto> getAllRoleOptionsForGym(String gymEmail) {
        Gym gym = gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));

        List<RoleOptionDto> options = new ArrayList<>();

        List<StaffRole> existingRoles = staffRoleRepository.findAllByGym(gym);

        Set<String> existingRoleNames = existingRoles.stream()
                .map(role -> role.getName().toUpperCase())
                .collect(Collectors.toSet());

        for (StaffRole role : existingRoles) {
            options.add(new RoleOptionDto(
                    "existing:" + role.getId(),
                    role.getName(),
                    "existing",
                    role.getName() + " (Custom)"
            ));
        }

        for (StaffPosition predefinedRole : StaffPosition.values()) {
            if (!existingRoleNames.contains(predefinedRole.name().toUpperCase())) {
                options.add(new RoleOptionDto(
                        "predefined:" + predefinedRole.name(),
                        predefinedRole.name(),
                        "predefined",
                        predefinedRole.name() + " (Predefined)"
                ));
            }
        }

        return options;

    }

    private StaffMemberResponseDto createSingleStaffMember(StaffMemberRequestDto request, Gym gym) {
        validateStaffMemberUniqueness(request);

        StaffRole staffRole = resolveStaffRole(request.getRoleSelection(), gym);

        StaffMember staffMember = new StaffMember();
        staffMember.setFirstName(request.getFirstName());
        staffMember.setLastName(request.getLastName());
        staffMember.setEmail(request.getEmail());
        staffMember.setPhone(request.getPhone());
        staffMember.setUsername(request.getUsername());
        staffMember.setPassword(passwordEncoder.encode(generateDefaultPassword()));
        staffMember.setGym(gym);
        staffMember.setStaffRole(staffRole);
        staffMember.setEnabled(true);


        StaffMember savedStaffMember = staffMemberRepository.save(staffMember);

        StaffMemberResponseDto response = modelMapper.map(savedStaffMember, StaffMemberResponseDto.class);
        response.setPasswordGenerated(true);
        response.setPasswordNote("Temporary password generated. Please change on first login.");

        return response;
    }

    private String generateDefaultPassword() {
        return "Staff" + System.currentTimeMillis() + "!";
    }

    private StaffRole resolveStaffRole(String roleSelection, Gym gym) {
        if (roleSelection == null || !roleSelection.contains(":")) {
            throw new FitManageAppException("Invalid role selection format", ApiErrorCode.BAD_REQUEST);
        }

        String[] parts = roleSelection.split(":", 2);
        String type = parts[0];
        String value = parts[1];

        return switch (type) {
            case "existing" -> getExistingStaffRole(Long.parseLong(value), gym);
            case "predefined" -> createFromPredefinedRole(Long.parseLong(value), gym);
            case "custom" -> createCustomRole(value, gym);
            default -> throw new FitManageAppException("Invalid role selection type", ApiErrorCode.BAD_REQUEST);
        };
    }

    private StaffRole createCustomRole(String roleName, Gym gym) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new FitManageAppException("Custom role name cannot be empty", ApiErrorCode.BAD_REQUEST);
        }

        Optional<StaffRole> existingRole = staffRoleRepository.findByNameAndGym(roleName, gym);
        if (existingRole.isPresent()) {
            return existingRole.get();
        }

        StaffRole newRole = new StaffRole();
        newRole.setName(roleName);
        newRole.setGym(gym);
        newRole.setPredefinedStaffRole(null);
        newRole.setPermissions(Set.of());

        return staffRoleRepository.save(newRole);
    }

    private StaffRole getExistingStaffRole(Long roleId, Gym gym) {
        return staffRoleRepository.findById(roleId)
                .filter(role -> role.getGym().getId().equals(gym.getId()))
                .orElseThrow(() -> new FitManageAppException("Staff role not found", ApiErrorCode.NOT_FOUND));
    }

    private StaffRole createFromPredefinedRole(Long predefinedRoleId, Gym gym) {
        PredefinedStaffRole template = predefinedStaffRoleRepository.findById(predefinedRoleId)
                .orElseThrow(() -> new FitManageAppException("Predefined role not found", ApiErrorCode.NOT_FOUND));

        Optional<StaffRole> existingRole = staffRoleRepository.findByPredefinedStaffRoleAndGym(template, gym);
        if (existingRole.isPresent()) {
            return existingRole.get();
        }

        StaffRole newRole = new StaffRole();
        newRole.setName(template.getName());
        newRole.setGym(gym);
        newRole.setPredefinedStaffRole(template);
        newRole.setPermissions(template.getDefaultPermissions());

        return staffRoleRepository.save(newRole);
    }


    private void validateStaffMemberUniqueness(StaffMemberRequestDto request) {
        Map<String, String> errors = new HashMap<>();

        if (staffMemberRepository.existsByUsername(request.getUsername())) {
            errors.put("username", "Username already exists");
        }

        if (staffMemberRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "Email already exists");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }
}
