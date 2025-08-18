package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberFilterRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberSubscriptionRequestDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberResponseDto;
import demos.springdata.fitmanage.domain.dto.gymmember.response.GymMemberTableDto;
import demos.springdata.fitmanage.domain.dto.gymmember.request.GymMemberUpdateRequestDto;
import demos.springdata.fitmanage.domain.dto.user.UserCreateRequestDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    GymMemberResponseDto createAndSaveNewMember(UserCreateRequestDto requestDto);
    List<GymMemberTableDto> getAllGymMembersForTable();
    GymMemberResponseDto updateMemberDetails(Long memberId, GymMemberUpdateRequestDto memberUpdateRequestDto);
    void removeGymMember(Long memberId);
    List<GymMemberTableDto> getGymMembersByFilter(GymMemberFilterRequestDto gymMemberFilterRequestDto);
    Optional<GymMemberResponseDto> findBySmartQuery(String input, Long gymId);
    GymMemberResponseDto checkInMember(String input, Long id);
    GymMemberResponseDto initializeSubscription(Long id, GymMemberSubscriptionRequestDto requestDto);
}
