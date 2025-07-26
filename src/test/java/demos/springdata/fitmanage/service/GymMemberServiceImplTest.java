package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberCreateRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.domain.entity.GymMember;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.domain.enums.SubscriptionStatus;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.exception.MultipleValidationException;
import demos.springdata.fitmanage.repository.GymMemberRepository;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.impl.GymMemberServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GymMemberServiceImplTest {
    @Mock
    private GymMemberRepository gymMemberRepository;

    @Mock
    private GymRepository gymRepository;

    @Mock
    private RoleService roleService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private GymMemberServiceImpl gymMemberService;

    private GymMemberCreateRequestDto requestDto;
    private GymMember gymMemberEntity;
    private Gym gym;
    private Role memberRole;


    @BeforeEach
    void setup() {
        requestDto = new GymMemberCreateRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPhone("123456789");
        gymMemberEntity = new GymMember();
        gymMemberEntity.setEmail("test@example.com");
        gymMemberEntity.setPhone("0877202011");
        gymMemberEntity.setRoles(new HashSet<>());

        gym = new Gym();

        memberRole = new Role();
        memberRole.setName(RoleType.MEMBER);
    }


    @Test
    void shouldCreateAndSaveNewMemberSuccessfully() {
        String gymEmail = "test@gym.com";

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(authentication.getName()).thenReturn(gymEmail);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        GymMember mappedMember = new GymMember();
        mappedMember.setEmail(requestDto.getEmail());
        mappedMember.setPassword(passwordEncoder.encode("123"));
        mappedMember.setPhone(requestDto.getPhone());
        mappedMember.setRoles(new HashSet<>());

        GymMember savedMember = new GymMember();
        savedMember.setId(1L);
        savedMember.setEmail(requestDto.getEmail());

        GymMemberResponseDto responseDto = new GymMemberResponseDto();

        Mockito.when(gymRepository.findByEmail(gymEmail)).thenReturn(Optional.of(gym));
        Mockito.when(modelMapper.map(requestDto, GymMember.class)).thenReturn(mappedMember);
        Mockito.when(roleService.findByName(RoleType.MEMBER)).thenReturn(memberRole);
        Mockito.when(gymMemberRepository.existsByPhoneAndGymEmail(requestDto.getEmail(), gymEmail)).thenReturn(false);
        Mockito.when(gymMemberRepository.existsByPhone(requestDto.getPhone())).thenReturn(false);
        Mockito.when(gymMemberRepository.save(mappedMember)).thenReturn(savedMember);
        Mockito.when(modelMapper.map(savedMember, GymMemberResponseDto.class)).thenReturn(responseDto);

        GymMemberResponseDto result = gymMemberService.createAndSaveNewMember(requestDto);

        Assertions.assertEquals(responseDto, result);
        Mockito.verify(gymMemberRepository).save(mappedMember);
    }


    @Test
    void shouldThrowExceptionWhenRemovingNonexistentMember() {
        Long memberId = 99L;
        Mockito.when(gymMemberRepository.findById(memberId)).thenReturn(Optional.empty());

        FitManageAppException ex = Assertions.assertThrows(
                FitManageAppException.class,
                () -> gymMemberService.removeGymMember(memberId)
        );

        Assertions.assertEquals(ApiErrorCode.NOT_FOUND, ex.getErrorCode());
    }



    @Test
    void shouldReturnAllExistingMembers() {
        String gymEmail = "test@gym.com";
        Gym gym = new Gym();
        gym.setEmail(gymEmail);

        GymMember member1 = new GymMember();
        GymMember member2 = new GymMember();

        GymMemberTableDto dto1 = new GymMemberTableDto();
        GymMemberTableDto dto2 = new GymMemberTableDto();

        List<GymMember> gymMembers = List.of(member1, member2);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(gymEmail);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(gymRepository.findByEmail(gymEmail)).thenReturn(Optional.of(gym));

        Mockito.when(gymMemberRepository.findGymMembersByGym(gym)).thenReturn(gymMembers);
        Mockito.when(modelMapper.map(member1, GymMemberTableDto.class)).thenReturn(dto1);
        Mockito.when(modelMapper.map(member2, GymMemberTableDto.class)).thenReturn(dto2);

        List<GymMemberTableDto> result = gymMemberService.getAllGymMembersForTable();

        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(dto1));
        Assertions.assertTrue(result.contains(dto2));

        Mockito.verify(gymMemberRepository).findGymMembersByGym(gym);
        Mockito.verify(modelMapper).map(member1, GymMemberTableDto.class);
        Mockito.verify(modelMapper).map(member2, GymMemberTableDto.class);
        Mockito.verify(gymRepository).findByEmail(gymEmail);
    }


    @Test
    void shouldUpdateMemberDetailsSuccessfully() {
        Long memberId = 1L;
        GymMember existingMember = new GymMember();
        existingMember.setId(memberId);
        existingMember.setPhone("0888123456");

        GymMemberUpdateRequestDto updateDto = new GymMemberUpdateRequestDto();
        updateDto.setFirstName("Old");
        updateDto.setLastName("OldName");
        updateDto.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
        updateDto.setPhone("0877202011");

        GymMember updatedMember = new GymMember();
        updatedMember.setFirstName("Updated");
        updatedMember.setLastName("NewName");
        updatedMember.setSubscriptionStatus(SubscriptionStatus.INACTIVE);
        updatedMember.setPhone("0888999999");

        GymMemberResponseDto responseDto = new GymMemberResponseDto();

        Mockito.when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        Mockito.when(gymMemberRepository.existsByPhone(updateDto.getPhone())).thenReturn(false);
        Mockito.when(gymMemberRepository.save(existingMember)).thenReturn(updatedMember);
        Mockito.when(modelMapper.map(updatedMember, GymMemberResponseDto.class)).thenReturn(responseDto);


        GymMemberResponseDto result = gymMemberService.updateMemberDetails(memberId, updateDto);


        Assertions.assertEquals(responseDto, result);
        Mockito.verify(gymMemberRepository).findById(memberId);
        Mockito.verify(gymMemberRepository).existsByPhone(updateDto.getPhone());
        Mockito.verify(gymMemberRepository).save(existingMember);
        Mockito.verify(modelMapper).map(updatedMember, GymMemberResponseDto.class);
    }

    @Test
    void shouldThrowException_WhenMemberNotFound() {
        Long memberId = 42L;
        GymMemberUpdateRequestDto dto = new GymMemberUpdateRequestDto();

        Mockito.when(gymMemberRepository.findById(memberId)).thenReturn(Optional.empty());

        FitManageAppException exception = Assertions.assertThrows(
                FitManageAppException.class,
                () -> gymMemberService.updateMemberDetails(memberId, dto)
        );

        Assertions.assertEquals(ApiErrorCode.NOT_FOUND, exception.getErrorCode());
        Mockito.verify(gymMemberRepository).findById(memberId);
    }

    @Test
    void shouldThrowException_WhenPhoneAlreadyTaken() {
        Long memberId = 1L;
        GymMember existingMember = new GymMember();
        existingMember.setId(memberId);
        existingMember.setPhone("0899240177");

        GymMemberUpdateRequestDto updateDto = new GymMemberUpdateRequestDto();
        updateDto.setPhone("0877202011");

        Mockito.when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));
        Mockito.when(gymMemberRepository.existsByPhone(updateDto.getPhone())).thenReturn(true);

        MultipleValidationException ex = Assertions.assertThrows(
                MultipleValidationException.class,
                () -> gymMemberService.updateMemberDetails(memberId, updateDto)
        );

        Assertions.assertTrue(ex.getErrors().containsKey("Phone"));
        Mockito.verify(gymMemberRepository).existsByPhone(updateDto.getPhone());

    }

    @Test
    void shouldRemoveGymMemberSuccessfully() {
        Long memberId = 1L;
        GymMember mockMember = new GymMember();
        mockMember.setId(memberId);

        Mockito.when(gymMemberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));
        gymMemberService.removeGymMember(memberId);

        Mockito.verify(gymMemberRepository).findById(memberId);
        Mockito.verify(gymMemberRepository).delete(mockMember);
    }



}
