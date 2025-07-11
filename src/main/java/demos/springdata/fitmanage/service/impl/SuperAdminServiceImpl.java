package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.superadmin.SuperAdminDto;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.SuperAdminRepository;
import demos.springdata.fitmanage.service.SuperAdminService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    private final SuperAdminRepository superAdminRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(SuperAdminServiceImpl.class);

    @Autowired
    public SuperAdminServiceImpl(SuperAdminRepository superAdminRepository, ModelMapper modelMapper) {
        this.superAdminRepository = superAdminRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public Optional<SuperAdminDto> getByEmail(String email) {
        LOGGER.info("Searching for super admin user with email: {}", email);
        return superAdminRepository.findByEmail(email)
                .map(superAdminUser -> this.modelMapper.map(superAdminUser, SuperAdminDto.class));
    }
}
