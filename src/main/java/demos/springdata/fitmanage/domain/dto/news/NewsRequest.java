package demos.springdata.fitmanage.domain.dto.news;

import demos.springdata.fitmanage.domain.enums.NewsImportance;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsRequest {
    private String title;
    private String content;
    private Set<String> targetRoles;
    private Set<UUID> recipientsIds;
    private PublicationType publicationType;
    private NewsImportance importance;
    private boolean targetSpecific;
    private Instant expiresOn;
}
