package demos.springdata.fitmanage.domain.dto.mapper;

import demos.springdata.fitmanage.domain.dto.tenant.TenantDto;
import demos.springdata.fitmanage.domain.dto.tenant.TenantLookUp;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.util.UserRoleHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TenantMapper {


    @Mapping(source = "id", target = "tenantId")
    TenantLookUp lookUp(Tenant tenant);


    @Mapping(target = "membersCount", source = "tenant", qualifiedByName = "calculateMembersCount")
    TenantDto toResponse(Tenant tenant);

    @Named("calculateMembersCount")
    default Long calculateMembersCount(Tenant tenant) {
        if (tenant.getUsers() == null) {
            return 0L;
        }

        return tenant.getUsers().stream()
                .filter(user -> UserRoleHelper.hasRole(user, RoleType.MEMBER))
                .count();
    }

}
