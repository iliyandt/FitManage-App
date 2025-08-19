package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.tenant.TenantResponseDto;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.repository.TenantRepository;
import demos.springdata.fitmanage.service.TenantService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final ModelMapper modelMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Autowired
    public TenantServiceImpl(TenantRepository tenantRepository, ModelMapper modelMapper) {
        this.tenantRepository = tenantRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<Tenant> getTenantByEmail(String email) {
        return tenantRepository.findTenantByUserEmail(email);
    }

    @Transactional
    @Override
    public List<TenantResponseDto> getAllTenants() {
        LOGGER.info("Retrieving all tenants..");
        return this.tenantRepository.findAll()
                .stream()
                .map(tenant -> this.modelMapper.map(tenant, TenantResponseDto.class))
                .toList();
    }
}
