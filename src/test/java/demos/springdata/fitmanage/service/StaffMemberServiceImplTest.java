package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.team.StaffMemberTableDto;
import demos.springdata.fitmanage.domain.entity.StaffMember;
import demos.springdata.fitmanage.repository.StaffMemberRepository;
import demos.springdata.fitmanage.service.impl.StaffMemberServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class StaffMemberServiceImplTest {
    @Mock
    private StaffMemberRepository staffMemberRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StaffMemberServiceImpl staffMemberService;


    @Test
    void getStaffMemberShouldReturnListWithAllMembers() {
        StaffMember staff1 = new StaffMember();
        staff1.setId(1L);
        StaffMember staff2 = new StaffMember();
        staff2.setId(2L);

        List<StaffMember> staffList = Arrays.asList(staff1, staff2);

        StaffMemberTableDto dto1 = new StaffMemberTableDto();
        dto1.setId(1L);
        StaffMemberTableDto dto2 = new StaffMemberTableDto();
        dto2.setId(2L);

        Mockito.when(staffMemberRepository.findAll()).thenReturn(staffList);
        Mockito.when(modelMapper.map(staff1, StaffMemberTableDto.class)).thenReturn(dto1);
        Mockito.when(modelMapper.map(staff2, StaffMemberTableDto.class)).thenReturn(dto2);

        List<StaffMemberTableDto> result = staffMemberService.getStaffMembersTableData();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(1L, result.get(0).getId());
        Assertions.assertEquals(2L, result.get(1).getId());

        Mockito.verify(staffMemberRepository, times(1)).findAll();
        Mockito.verify(modelMapper, times(1)).map(staff1, StaffMemberTableDto.class);
        Mockito.verify(modelMapper, times(1)).map(staff2, StaffMemberTableDto.class);
    }


}
