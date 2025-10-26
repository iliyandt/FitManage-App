package demos.springdata.fitmanage.domain.dto.news;

import demos.springdata.fitmanage.domain.enums.NewsImportance;
import demos.springdata.fitmanage.domain.enums.NewsStatus;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import java.time.Instant;
import java.util.Set;

public class NewsResponse {
    private String title;
    private String content;
    private Long authorId;
    private Instant publishedAt;
    private NewsStatus status;
    private PublicationType publicationType;
    private Set<Long> recipientsIds;
    private NewsImportance importance;
    private boolean targetSpecific;

    public NewsResponse() {
    }

    public String getTitle() {
        return title;
    }

    public NewsResponse setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public NewsResponse setContent(String content) {
        this.content = content;
        return this;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public NewsResponse setAuthorId(Long authorId) {
        this.authorId = authorId;
        return this;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public NewsResponse setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public NewsStatus getStatus() {
        return status;
    }

    public NewsResponse setStatus(NewsStatus status) {
        this.status = status;
        return this;
    }

    public PublicationType getPublicationType() {
        return publicationType;
    }

    public NewsResponse setPublicationType(PublicationType publicationType) {
        this.publicationType = publicationType;
        return this;
    }

    public Set<Long> getRecipientsIds() {
        return recipientsIds;
    }

    public NewsResponse setRecipientsIds(Set<Long> recipientsIds) {
        this.recipientsIds = recipientsIds;
        return this;
    }

    public NewsImportance getImportance() {
        return importance;
    }

    public NewsResponse setImportance(NewsImportance importance) {
        this.importance = importance;
        return this;
    }

    public boolean isTargetSpecific() {
        return targetSpecific;
    }

    public NewsResponse setTargetSpecific(boolean targetSpecific) {
        this.targetSpecific = targetSpecific;
        return this;
    }
}
