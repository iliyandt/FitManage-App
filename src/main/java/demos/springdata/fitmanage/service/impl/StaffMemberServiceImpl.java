package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.team.StaffMemberTableDto;
import demos.springdata.fitmanage.domain.entity.StaffMember;
import demos.springdata.fitmanage.repository.StaffMemberRepository;
import demos.springdata.fitmanage.service.StaffMemberService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffMemberServiceImpl implements StaffMemberService {

    private final StaffMemberRepository staffMemberRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public StaffMemberServiceImpl(StaffMemberRepository staffMemberRepository, ModelMapper modelMapper) {
        this.staffMemberRepository = staffMemberRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<StaffMemberTableDto> getStaffMembersTableData() {
        List<StaffMember> staffMembers = staffMemberRepository.findAll();
        return staffMembers.stream()
                .map(staffMember -> modelMapper.map(staffMember, StaffMemberTableDto.class))
                .toList();
    }
}
