package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.news.NewsRequest;
import demos.springdata.fitmanage.domain.dto.news.NewsResponse;
import demos.springdata.fitmanage.domain.entity.News;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.NewsStatus;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.NewsRepository;
import demos.springdata.fitmanage.service.NewsService;
import demos.springdata.fitmanage.service.UserService;
import demos.springdata.fitmanage.util.CurrentUserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserService userService;
    private final CurrentUserUtils currentUser;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, UserService userService, CurrentUserUtils currentUser) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.currentUser = currentUser;
    }

    @Override
    public NewsResponse createNews(NewsRequest request) {

        User user = currentUser.getCurrentUser();

        News news = new News()
                .setTitle(request.getTitle())
                .setContent(request.getContent())
                .setAuthor(user)
                .setPublishedAt(Instant.now())
                .setStatus(NewsStatus.PUBLISHED)
                .setPublicationType(request.getPublicationType());

        Set<Long> recipientsIds = request.getRecipientsIds();
        if (request.getPublicationType() == PublicationType.TARGETED &&
                recipientsIds != null &&
                !recipientsIds.isEmpty()) {

            List<User> targetedRecipients = userService.findAllUserFromCollectionOfIds(recipientsIds);

            news.setRecipients(new HashSet<>(targetedRecipients));

        } else if (request.getPublicationType() == PublicationType.TARGETED && recipientsIds == null) {
            throw new FitManageAppException("Targeted news must specify at least one recipient ID.", ApiErrorCode.CONFLICT);
        }

        newsRepository.save(news);

        return mapToDto(news, user.getId());
    }


    @Override
    public List<NewsResponse> getNewsForUser() {

        User user = currentUser.getCurrentUser();

        List<News> news = newsRepository.findAllPublishedForUser(user.getId(), NewsStatus.PUBLISHED);

        return news.stream()
                .map(post -> mapToDto(post, user.getId()))
                .toList();
    }


    private NewsResponse mapToDto(News news, Long authorId) {
        return  new NewsResponse()
                .setTitle(news.getTitle())
                .setContent(news.getContent())
                .setAuthorId(authorId)
                .setPublishedAt(news.getPublishedAt())
                .setStatus(news.getStatus())
                .setPublicationType(news.getPublicationType());
    }
}
