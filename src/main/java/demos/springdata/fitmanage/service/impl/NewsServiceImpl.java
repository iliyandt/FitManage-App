package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.news.NewsRequest;
import demos.springdata.fitmanage.domain.dto.news.NewsResponse;
import demos.springdata.fitmanage.domain.entity.News;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.NewsStatus;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.ApiErrorCode;
import demos.springdata.fitmanage.exception.FitManageAppException;
import demos.springdata.fitmanage.repository.NewsRepository;
import demos.springdata.fitmanage.service.NewsService;
import demos.springdata.fitmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserService userService;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, UserService userService) {
        this.newsRepository = newsRepository;
        this.userService = userService;

    }

    @Override
    public NewsResponse createNews(NewsRequest request) {

        User user = userService.getCurrentUser();
        News news = new News()
                .setTitle(request.getTitle())
                .setContent(request.getContent())
                .setTenantId(user.getTenant().getId())
                .setAuthor(user)
                .setPublishedAt(Instant.now())
                .setStatus(NewsStatus.PUBLISHED)
                .setPublicationType(request.getPublicationType())
                .setImportance(request.getImportance())
                .setTargetSpecific(request.isTargetSpecific())
                .setExpiresOn(request.getExpiresOn());

        News targetedNews = getTargetedUsers(request, news);

        newsRepository.save(targetedNews);
        return mapToDto(targetedNews);
    }




    @Override
    @Transactional
    public List<NewsResponse> getNewsForUser() {

        User user = userService.getCurrentUser();
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleType.ADMIN);

        List<News> news = newsRepository.findAllPublishedForUser(user.getId(), NewsStatus.PUBLISHED, user.getTenant().getId(), isAdmin);

        return news.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public NewsResponse delete(Long newsId) {
        News newsToDelete = newsRepository.getNewsById(newsId);
        newsRepository.delete(newsToDelete);
        return mapToDto(newsToDelete);
    }

    @Override
    @Transactional
    public NewsResponse update(Long newsId, NewsRequest request) {
        News newsToUpdate = newsRepository.getNewsById(newsId);
        newsToUpdate
                .setTitle(request.getTitle())
                .setContent(request.getContent())
                .setPublicationType(request.getPublicationType())
                .setImportance(request.getImportance())
                .setTargetSpecific(request.isTargetSpecific())
                .setExpiresOn(request.getExpiresOn());


        News updated = getTargetedUsers(request, newsToUpdate);
        return mapToDto(updated);
    }

    private News getTargetedUsers(NewsRequest request, News news) {

        Set<Long> recipientsIds = request.getRecipientsIds();
        Set<RoleType> targetRoles = request.getTargetRoles();

        if (request.getPublicationType() == PublicationType.TARGETED) {

            Set<User> targetedUsers = userService.findAllUsersByIdsOrRoles(recipientsIds, targetRoles, news.getAuthor().getTenant().getId());

            news.setRecipients(targetedUsers);

        } else if (request.getPublicationType() == PublicationType.TARGETED && (targetRoles == null || recipientsIds == null)) {
            throw new FitManageAppException("Targeted news must specify at least one role or recipient ID.", ApiErrorCode.CONFLICT);
        }

        return news;
    }


    private NewsResponse mapToDto(News news) {

        Set<User> recipients = news.getRecipients();

        Set<Long> recipientIds = new HashSet<>();

        Set<RoleType> targetedRoles = recipients.stream()
                .flatMap(user -> user.getRoles().stream())
                .map(Role::getName)
                .collect(Collectors.toSet());

        if (!recipients.isEmpty()) {
            recipientIds = recipients.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
        }

        return  new NewsResponse()
                .setNewsId(news.getId())
                .setTitle(news.getTitle())
                .setContent(news.getContent())
                .setAuthorId(news.getAuthor().getId())
                .setPublishedAt(news.getPublishedAt())
                .setStatus(news.getStatus())
                .setPublicationType(news.getPublicationType())
                .setRecipientsIds(recipientIds)
                .setImportance(news.getImportance())
                .setExpiresOn(news.getExpiresOn())
                .setTargetSpecific(news.isTargetSpecific())
                .setTargetRoles(targetedRoles);
    }
}
