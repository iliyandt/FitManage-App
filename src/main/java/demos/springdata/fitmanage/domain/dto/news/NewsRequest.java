package demos.springdata.fitmanage.domain.dto.news;
import demos.springdata.fitmanage.domain.enums.PublicationType;

import java.util.Set;

public class NewsRequest {
    private String title;
    private String content;
    private Set<Long> recipientsIds;
    private PublicationType publicationType;

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
}
