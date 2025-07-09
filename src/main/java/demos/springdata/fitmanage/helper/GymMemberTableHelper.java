package demos.springdata.fitmanage.helper;

import demos.springdata.fitmanage.domain.dto.ActionConfigDto;
import demos.springdata.fitmanage.domain.dto.ConfigDto;
import demos.springdata.fitmanage.domain.dto.PaginationConfigDto;
import demos.springdata.fitmanage.domain.dto.gymmember.GymMemberTableDto;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GymMemberTableHelper {
    public List<Map<String, Object>> buildRows(List<GymMemberTableDto> members) {
        return members.stream()
                .map(this::buildRowMap)
                .toList();
    }

    public ConfigDto buildTableConfig() {
        PaginationConfigDto pagination = new PaginationConfigDto();
        pagination.setPageSize(10);

        List<ActionConfigDto> actions = List.of(
                new ActionConfigDto("details", "Details", "gym-members/{id}"),
                new ActionConfigDto("edit", "Edit", "gym-members/{id}"),
                new ActionConfigDto("delete", "Delete", "gym-members/{id}")
        );

        ConfigDto config = new ConfigDto();
        config.setSortable(true);
        config.setActions(actions);
        config.setPagination(pagination);

        return config;
    }

    private Map<String, Object> buildRowMap(GymMemberTableDto member) {
        Map<String, Object> row = new LinkedHashMap<>();

        row.put("id", member.getId());
        row.put("fullName", member.getFullName());
        row.put("subscriptionStatus", member.getSubscriptionStatus() != null ? member.getSubscriptionStatus() : "No Subscription");
        row.put("phone", member.getPhone());

        return row;
    }
}
