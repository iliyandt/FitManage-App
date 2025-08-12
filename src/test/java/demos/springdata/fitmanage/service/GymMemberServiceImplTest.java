package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberTableDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.impl.GymMemberServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymMemberServiceImplTest {

    @Mock private GymMemberRepository gymMemberRepository;
    @Mock private GymRepository gymRepository;
    @Mock private RoleService roleService;
    @Mock private VisitService visitService;
    @Mock private ModelMapper modelMapper;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private GymMemberServiceImpl service;

    private Gym gym;

    @BeforeEach
    void setUp() {
        gym = new Gym(gym.getId());
        gym.setEmail("gym@example.com");
        gym.setUsername("gymuser");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("gym@example.com", "pwd")
        );
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllGymMembersForTable_returnsMappedDtos() {
        when(gymRepository.findByEmail("gym@example.com")).thenReturn(Optional.of(gym));
        GymMember member1 = new GymMember();
        GymMember member2 = new GymMember();
        when(gymMemberRepository.findGymMembersByGym(gym)).thenReturn(List.of(member1, member2));

        GymMemberTableDto dto1 = new GymMemberTableDto();
        GymMemberTableDto dto2 = new GymMemberTableDto();
        when(modelMapper.map(member1, GymMemberTableDto.class)).thenReturn(dto1);
        when(modelMapper.map(member2, GymMemberTableDto.class)).thenReturn(dto2);

        List<GymMemberTableDto> result = service.getAllGymMembersForTable();

        assertEquals(2, result.size());
        verify(gymMemberRepository).findGymMembersByGym(gym);
    }

    @Test
    void getGymMembersByFilter_throws_whenEmpty() {
        when(gymRepository.findByEmail("gym@example.com")).thenReturn(Optional.of(gym));
        when(gymMemberRepository.findAll(org.mockito.Mockito.<Specification<GymMember>>any()))
                .thenReturn(List.of());

        FitManageAppException ex = assertThrows(FitManageAppException.class,
                () -> service.getGymMembersByFilter(new GymMemberFilterRequestDto()));

        assertEquals(ApiErrorCode.NOT_FOUND, ex.getErrorCode());
    }
}
