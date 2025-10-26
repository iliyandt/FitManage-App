package demos.springdata.fitmanage.domain.dto.news;
import demos.springdata.fitmanage.domain.enums.NewsImportance;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import demos.springdata.fitmanage.domain.enums.RoleType;

import java.util.Set;

public class NewsRequest {
    private String title;
    private String content;
    private Set<RoleType> targetRoles;
    private Set<Long> recipientsIds;
    private PublicationType publicationType;
    private NewsImportance importance;
    private boolean targetSpecific;


    public NewsRequest() {
    }

    public String getTitle() {
        return title;
    }

    public NewsRequest setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public NewsRequest setContent(String content) {
        this.content = content;
        return this;
    }

    public Set<RoleType> getTargetRoles() {
        return targetRoles;
    }

    public NewsRequest setTargetRoles(Set<RoleType> targetRoles) {
        this.targetRoles = targetRoles;
        return this;
    }

    public Set<Long> getRecipientsIds() {
        return recipientsIds;
    }

    public NewsRequest setRecipientsIds(Set<Long> recipientsIds) {
        this.recipientsIds = recipientsIds;
        return this;
    }

    public PublicationType getPublicationType() {
        return publicationType;
    }

    public NewsRequest setPublicationType(PublicationType publicationType) {
        this.publicationType = publicationType;
        return this;
    }

    public NewsImportance getImportance() {
        return importance;
    }

    public NewsRequest setImportance(NewsImportance importance) {
        this.importance = importance;
        return this;
    }

    public boolean isTargetSpecific() {
        return targetSpecific;
    }

    public NewsRequest setTargetSpecific(boolean targetSpecific) {
        this.targetSpecific = targetSpecific;
        return this;
    }
}
