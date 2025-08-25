package demos.springdata.fitmanage.domain.dto.tenant.users;

public final class StaffResponseDto extends UserBaseResponseDto {
    private Integer membersCount;

    public StaffResponseDto() {
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public StaffResponseDto setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
        return this;
    }
}
