package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.tenant.users.member.request.MemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.tenant.users.member.response.MemberResponseDto;
import demos.springdata.fitmanage.service.MembershipService;
import org.springframework.stereotype.Service;

@Service
public class MembershipServiceImpl implements MembershipService {
    @Override
    public MemberResponseDto initializeSubscription(Long memberId, MemberSubscriptionRequestDto requestDto) {
        return null;
    }
}
