package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.superadmin.SuperAdminDto;
import demos.springdata.fitmanage.domain.entity.SuperAdminUser;
import demos.springdata.fitmanage.repository.SuperAdminRepository;
import demos.springdata.fitmanage.service.impl.SuperAdminServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class SuperAdminServiceImplTest {
    private SuperAdminRepository superAdminRepository;
    private SuperAdminServiceImpl superAdminService;
    private ModelMapper modelMapper;


    @BeforeEach
    void setUp() {
        superAdminRepository = mock(SuperAdminRepository.class);
        modelMapper = mock(ModelMapper.class);
        superAdminService = new SuperAdminServiceImpl(superAdminRepository, modelMapper);
    }

    @Test
    void findByEmail_ShouldReturnSuperAdminDto_whenEmailExists() {
        String email = "admin@example.com";
        SuperAdminUser superAdminUser = new SuperAdminUser();
        superAdminUser.setEmail(email);

        SuperAdminDto superAdminDto = new SuperAdminDto();
        superAdminDto.setEmail(email);

        when(superAdminRepository.findByEmail(email)).thenReturn(Optional.of(superAdminUser));
        when(modelMapper.map(superAdminUser, SuperAdminDto.class)).thenReturn(superAdminDto);

        SuperAdminDto result = superAdminService.findByEmail(email);


        assertNotNull(result);
        Assertions.assertEquals(email, result.getEmail());

        verify(superAdminRepository, times(1)).findByEmail(email);
        verify(modelMapper, times(1)).map(superAdminUser, SuperAdminDto.class);
    }
}
