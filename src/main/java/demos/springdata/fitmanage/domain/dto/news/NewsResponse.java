package demos.springdata.fitmanage.domain.dto.news;

import demos.springdata.fitmanage.domain.enums.NewsImportance;
import demos.springdata.fitmanage.domain.enums.NewsStatus;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import demos.springdata.fitmanage.domain.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewsResponse {
    private Long newsId;
    private String title;
    private String content;
    private Long authorId;
    private Instant publishedAt;
    private NewsStatus status;
    private PublicationType publicationType;
    private Set<Long> recipientsIds;
    private Set<RoleType> targetRoles;
    private NewsImportance importance;
    private boolean targetSpecific;
    private Instant expiresOn;
}
