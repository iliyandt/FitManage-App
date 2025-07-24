package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.team.response.RoleOptionDto;
import demos.springdata.fitmanage.domain.dto.team.request.StaffMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.team.response.StaffMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.team.response.StaffMemberTableDto;
import demos.springdata.fitmanage.domain.entity.*;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.StaffPosition;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.PredefinedStaffRoleRepository;
import demos.springdata.fitmanage.repository.StaffMemberRepository;
import demos.springdata.fitmanage.repository.StaffRoleRepository;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.service.StaffMemberService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StaffMemberServiceImpl implements StaffMemberService {

    private final GymRepository gymRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PredefinedStaffRoleRepository predefinedStaffRoleRepository;
    private final ModelMapper modelMapper;
    private final RoleService roleService;

    @Autowired
    public StaffMemberServiceImpl(GymRepository gymRepository, StaffRoleRepository staffRoleRepository, StaffMemberRepository staffMemberRepository, BCryptPasswordEncoder passwordEncoder, PredefinedStaffRoleRepository predefinedStaffRoleRepository, ModelMapper modelMapper, RoleService roleService) {
        this.gymRepository = gymRepository;
        this.staffRoleRepository = staffRoleRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.predefinedStaffRoleRepository = predefinedStaffRoleRepository;
        this.modelMapper = modelMapper;
        this.roleService = roleService;
    }

    @Override
    public List<StaffMemberTableDto> getStaffMembersTableData() {
        String gymEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Gym gym = gymRepository.findByEmail(gymEmail)
                .orElseThrow(() -> new FitManageAppException("Gym not found", ApiErrorCode.NOT_FOUND));

        List<StaffMember> staffMembers = staffMemberRepository.findAllByGymWithRole(gym);
        return staffMembers.stream()
                .map(StaffMemberTableDto::from)
                .toList();
    }

    @Override
    @Transactional
    public List<StaffMemberResponseDto> createStaffMembers(List<StaffMemberCreateRequestDto> requests, String gymEmail) {
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

        List<StaffRole> existingRoles = staffRoleRepository.findAllByGym(gym);

        Set<String> existingRoleNames = existingRoles.stream()
                .map(role -> role.getName().toUpperCase())
                .collect(Collectors.toSet());

        List<RoleOptionDto> existingOptions = existingRoles.stream()
                .map(role -> createRoleOptionDto("existing",
                        "existing:" + role.getId(),
                        role.getName(),
                        role.getName() + " (Custom)"))
                .toList();

        List<RoleOptionDto> predefinedOptions = Arrays.stream(StaffPosition.values())
                .filter(pos -> !existingRoleNames.contains(pos.name().toUpperCase()))
                .map(pos -> createRoleOptionDto("predefined",
                        "predefined:" + pos.name(),
                        pos.name(),
                        pos.name() + " (Predefined)"))
                .toList();

        return Stream.concat(existingOptions.stream(), predefinedOptions.stream())
                .toList();
    }

    private RoleOptionDto createRoleOptionDto(String type, String id, String name, String label) {
        return new RoleOptionDto(id, name, type, label);
    }

    private StaffMemberResponseDto createSingleStaffMember(StaffMemberCreateRequestDto request, Gym gym) {
        validateStaffMemberUniqueness(request);

        StaffRole staffRole = resolveStaffRole(request.getRoleSelection(), gym);

        StaffMember staffMember = new StaffMember()
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName())
                .setEmail(request.getEmail())
                .setPhone(request.getPhone())
                .setUsername(request.getUsername())
                .setPassword(passwordEncoder.encode(generateDefaultPassword()))
                .setGym(gym)
                .setStaffRole(staffRole)
                .setEnabled(true);

        assignStaffRole(staffMember);
        StaffMember savedStaffMember = staffMemberRepository.save(staffMember);

        StaffMemberResponseDto response = modelMapper.map(savedStaffMember, StaffMemberResponseDto.class);
        response.setPasswordGenerated(true);
        response.setPasswordNote("Temporary password generated. Please change on first login.");

        return response;
    }

    private void assignStaffRole(StaffMember staffMember) {
        Role staffMemberRole = roleService.findByName(RoleType.STAFF_MEMBER);
        staffMember.getRoles().add(staffMemberRole);
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

        StaffRole newRole = new StaffRole()
                .setName(template.getName())
                .setGym(gym)
                .setPredefinedStaffRole(template)
                .setPermissions(template.getDefaultPermissions());

        return staffRoleRepository.save(newRole);
    }


    private void validateStaffMemberUniqueness(StaffMemberCreateRequestDto request) {
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
