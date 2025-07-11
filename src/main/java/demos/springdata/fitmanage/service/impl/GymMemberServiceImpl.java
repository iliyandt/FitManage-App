package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.service.GymMemberService;
import demos.springdata.fitmanage.service.RoleService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GymMemberServiceImpl implements GymMemberService {
    private final GymMemberRepository gymMemberRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(GymMemberServiceImpl.class);

    @Autowired
    public GymMemberServiceImpl(GymMemberRepository gymMemberRepository, RoleService roleService, ModelMapper modelMapper) {
        this.gymMemberRepository = gymMemberRepository;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }

    @Override
    public GymMemberResponseDto registerMemberToGym(Gym gym, GymMemberCreateRequestDto requestDto) {
        Map<String, String> errors = new HashMap<>();

        validateCredentials(requestDto, errors);

        GymMember member = modelMapper.map(requestDto, GymMember.class);
        member.setGym(gym);
        Role gymAdminRole = roleService.findByName(RoleType.MEMBER);
        member.getRoles().add(gymAdminRole);

        LOGGER.info("Member with email {} will be saved in the database", member.getEmail());
        GymMember savedMember = gymMemberRepository.save(member);

        return modelMapper.map(savedMember, GymMemberResponseDto.class);
    }

    private void validateCredentials(GymMemberCreateRequestDto requestDto, Map<String, String> errors) {
        if (gymMemberRepository.existsByEmail(requestDto.getEmail())) {
            LOGGER.warn("Member with email {} already exists", requestDto.getEmail());
            errors.put("Email", "Email is already registered");
        }

        if (gymMemberRepository.existsByPhone(requestDto.getPhone())) {
            LOGGER.warn("Member with phone {} already exists", requestDto.getPhone());
            errors.put("Phone", "Phone used from another member");
        }

        if (!errors.isEmpty()) {
            throw new MultipleValidationException(errors);
        }
    }

    @Override
    public List<GymMemberTableDto> getAllGymMembersForTable() {
        List<GymMember> members = gymMemberRepository.findAll();
            return members.stream()
                    .map(member -> modelMapper.map(member, GymMemberTableDto.class))
                    .toList();
    }


    @Override
    public GymMemberResponseDto updateMemberDetails(Long memberId, GymMemberUpdateRequestDto memberUpdateRequestDto) {
        GymMember existingMember = gymMemberRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("Gym member not found", ApiErrorCode.NOT_FOUND));

        LOGGER.info("Updating member with ID {}", memberId);

        existingMember.setFirstName(memberUpdateRequestDto.getFirstName());
        existingMember.setLastName(memberUpdateRequestDto.getLastName());
        existingMember.setSubscriptionStatus(memberUpdateRequestDto.getSubscriptionStatus());

        if (gymMemberRepository.existsByPhone(memberUpdateRequestDto.getPhone()) &&
                !existingMember.getPhone().equals(memberUpdateRequestDto.getPhone())) {
            throw new MultipleValidationException(Map.of("Phone", "Phone used by another member"));
        }
        existingMember.setPhone(memberUpdateRequestDto.getPhone());

        GymMember updatedMember = gymMemberRepository.save(existingMember);
        LOGGER.info("Member with ID {} updated successfully", memberId);

        return mapToResponseDto(updatedMember);
    }

    @Override
    public void removeGymMember(Long memberId) {
        GymMember gymMember = gymMemberRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("Gym member not found", ApiErrorCode.NOT_FOUND));
        LOGGER.info("Deleting member with ID {}", memberId);

        gymMemberRepository.delete(gymMember);
        LOGGER.info("Member with ID {} deleted successfully", memberId);
    }

    private GymMemberResponseDto mapToResponseDto(GymMember updatedMember) {
        return modelMapper.map(updatedMember, GymMemberResponseDto.class);
    }
}
