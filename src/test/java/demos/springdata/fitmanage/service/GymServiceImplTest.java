package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gym.GymSummaryDto;
import demos.springdata.fitmanage.domain.entity.Gym;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.GymRepository;
import demos.springdata.fitmanage.service.impl.GymMemberServiceImpl;
import demos.springdata.fitmanage.service.impl.GymServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GymServiceImplTest {
    private GymRepository gymRepository;
    private GymMemberServiceImpl gymMemberService;
    private ModelMapper modelMapper;
    private GymServiceImpl gymService;


    @BeforeEach
    void setUp() {
        gymRepository = mock(GymRepository.class);
        modelMapper = new ModelMapper();
        gymService = new GymServiceImpl(gymRepository, modelMapper, gymMemberService);
    }


    @Test
    void getAllGyms_ShouldReturnListOfGymDtos() {
        Gym gym1 = new Gym();
        gym1.setEmail("gym1@example.com");

        Gym gym2 = new Gym();
        gym2.setEmail("gym2@example.com");

        when(gymRepository.findAll()).thenReturn(List.of(gym1, gym2));

        List<GymSummaryDto> responseDtoList = gymService.getAllGyms();

        assertEquals(2, responseDtoList.size());
        assertEquals("gym1@example.com", responseDtoList.get(0).getEmail());
    }


    @Test
    void getGymByEmail_ShouldReturnDto_WhenFound() {
        Gym gym = new Gym();
        gym.setEmail("gym@example.com");
        when(gymRepository.findByEmail("gym@example.com")).thenReturn(Optional.of(gym));

        GymSummaryDto responseDto = gymService.getGymByEmail("gym@example.com").orElseThrow();

        assertNotNull(responseDto);
        assertEquals("gym@example.com", responseDto.getEmail());
    }

    @Test
    void getGymByEmail_ShouldThrowException_WhenNotFound() {
        when(gymRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        FitManageAppException exception = assertThrows(FitManageAppException.class,
                () -> gymService.getGymByEmail("missing@example.com"));

        assertEquals("Gym not found.", exception.getMessage());
    }



}
