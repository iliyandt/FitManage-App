package demos.springdata.fitmanage.domain.entity;

import demos.springdata.fitmanage.domain.enums.NewsStatus;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


@Table(name = "news")
@Entity
public class News extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "news_status", nullable = false)
    private NewsStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_type", nullable = false)
    private PublicationType publicationType;

    @ManyToMany
    @JoinTable(
            name = "news_recipients",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> recipients = new HashSet<>();

    public News() {
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

    public Set<User> getRecipients() {
        return recipients;
    }

    public News setRecipients(Set<User> recipients) {
        this.recipients = recipients;
        return this;
    }
}
