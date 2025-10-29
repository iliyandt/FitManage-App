package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.NewsImportance;
import demos.springdata.fitmanage.domain.enums.NewsStatus;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


@Table(name = "news")
@Entity
public class News extends BaseEntity {

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    private boolean targetSpecific;

    private Instant expiresOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "news_status", nullable = false)
    private NewsStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_type", nullable = false)
    private PublicationType publicationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "importance")
    private NewsImportance importance;

    @ManyToMany
    @JoinTable(
            name = "news_recipients",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> recipients = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "news_recipients_roles",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


    public News() {
    }

    public Long getTenantId() {
        return tenantId;
    }

    public News setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public News setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public News setContent(String content) {
        this.content = content;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public News setAuthor(User author) {
        this.author = author;
        return this;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public News setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public boolean isTargetSpecific() {
        return targetSpecific;
    }

    public News setTargetSpecific(boolean targetSpecific) {
        this.targetSpecific = targetSpecific;
        return this;
    }

    public Instant getExpiresOn() {
        return expiresOn;
    }

    public News setExpiresOn(Instant expiresOn) {
        this.expiresOn = expiresOn;
        return this;
    }

    public NewsStatus getStatus() {
        return status;
    }

    public News setStatus(NewsStatus status) {
        this.status = status;
        return this;
    }

    public PublicationType getPublicationType() {
        return publicationType;
    }

    public News setPublicationType(PublicationType publicationType) {
        this.publicationType = publicationType;
        return this;
    }

    public NewsImportance getImportance() {
        return importance;
    }

    public News setImportance(NewsImportance importance) {
        this.importance = importance;
        return this;
    }

    public Set<User> getRecipients() {
        return recipients;
    }

    public News setRecipients(Set<User> recipients) {
        this.recipients = recipients;
        return this;
    }
}
