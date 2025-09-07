package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.shift.ShiftCreateRequest;
import demos.springdata.fitmanage.domain.dto.shift.ShiftResponseDto;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.repository.ShiftRepository;
import demos.springdata.fitmanage.service.ShiftService;
import demos.springdata.fitmanage.util.CurrentUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final CurrentUserUtils currentUserUtils;
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftServiceImpl.class);

    @Autowired
    public ShiftServiceImpl(ShiftRepository shiftRepository, CurrentUserUtils currentUserUtils) {
        this.shiftRepository = shiftRepository;
        this.currentUserUtils = currentUserUtils;
    }


    @Override
    public ShiftResponseDto createShift(ShiftCreateRequest createRequest) {
        User user = currentUserUtils.getCurrentUser();
        Tenant tenant = user.getTenant();
        return null;
    }

    @Override
    public List<ShiftResponseDto> getShiftsForCurrentUser() {
        LOGGER.info("Get shifts information for current user");
        return List.of();
    }
}
