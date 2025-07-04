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
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.service.GymMemberService;
import demos.springdata.fitmanage.service.RoleService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public GymMemberResponseDto createMemberForGym(Gym gym, GymMemberCreateRequestDto requestDto) {
        GymMember member = modelMapper.map(requestDto, GymMember.class);
        member.setGym(gym);
        Role gymAdminRole = roleService.findByName(RoleType.MEMBER);
        member.getRoles().add(gymAdminRole);
        LOGGER.info("member with email {} will be saved in the database", member.getEmail());
        GymMember savedMember = gymMemberRepository.save(member);

        LOGGER.info("the entity for member {} will be mapped to response dto", savedMember.getFirstName());
        return modelMapper.map(savedMember, GymMemberResponseDto.class);
    }

    @Override
    public List<GymMemberTableDto> findAllGymMembers() {
        List<GymMember> members = gymMemberRepository.findAll();
            return members.stream()
                    .map(member -> modelMapper.map(member, GymMemberTableDto.class))
                    .toList();
    }


    @Override
    public GymMemberResponseDto updateGymMember(Long memberId, GymMemberUpdateRequestDto memberUpdateRequestDto) {
        GymMember existingMember = gymMemberRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("Gym member not found", ApiErrorCode.NOT_FOUND));

        existingMember.setFirstName(memberUpdateRequestDto.getFirstName());
        existingMember.setLastName(memberUpdateRequestDto.getLastName());
        existingMember.setSubscriptionPlan(memberUpdateRequestDto.getSubscriptionPlan());
        existingMember.setPhone(memberUpdateRequestDto.getPhone());

        GymMember updatedMember = gymMemberRepository.save(existingMember);

        return mapToResponseDto(updatedMember);
    }

    @Override
    public void deleteGymMember(Long memberId) {
        GymMember gymMember = gymMemberRepository.findById(memberId)
                .orElseThrow(() -> new FitManageAppException("Gym member not found", ApiErrorCode.NOT_FOUND));
        gymMemberRepository.delete(gymMember);
    }

    private GymMemberResponseDto mapToResponseDto(GymMember updatedMember) {
        return modelMapper.map(updatedMember, GymMemberResponseDto.class);
    }
}
