package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymBasicInfoDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.repository.StaffMemberRepository;
import demos.springdata.fitmanage.service.impl.GymOnboardingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GymOnboardingServiceImplTest {
    @Mock
    private GymRepository gymRepository;
    @Mock
    private StaffMemberRepository staffMemberRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private GymOnboardingServiceImpl gymOnboardingService;


    @Test
    void shouldUpdateGymBasicInfoSuccessfully() {
        String gymEmail = "test@gym.bg";
        GymBasicInfoDto dto = new GymBasicInfoDto();
        dto.setUsername("newUsername");
        dto.setEmail("new@gym.com");
        dto.setPhone("0888123456");
        dto.setAddress("New Street");
        dto.setCity("Sofia");

        Gym existingGym = new Gym();
        existingGym.setId(1L);
        existingGym.setUsername("oldUsername");
        existingGym.setEmail(gymEmail);
        existingGym.setPhone("0888111222");
        existingGym.setAddress("Old St.");
        existingGym.setCity("Plovdiv");

        Mockito.when(gymRepository.findByEmail(gymEmail)).thenReturn(Optional.of(existingGym));
        Mockito.when(gymRepository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());

        gymOnboardingService.updateGymBasicInfo(gymEmail, dto);

        Mockito.verify(gymRepository).findByEmail(gymEmail);
        Mockito.verify(gymRepository).findByUsername(dto.getUsername());
        Mockito.verify(gymRepository).save(existingGym);

        Assertions.assertEquals(dto.getUsername(), existingGym.getActualUsername());
        Assertions.assertEquals(dto.getEmail(), existingGym.getEmail());
        Assertions.assertEquals(dto.getPhone(), existingGym.getPhone());
        Assertions.assertEquals(dto.getAddress(), existingGym.getAddress());
        Assertions.assertEquals(dto.getCity(), existingGym.getCity());
    }

}
