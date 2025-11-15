package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.news.NewsRequest;
import demos.springdata.fitmanage.domain.dto.news.NewsResponse;
import demos.springdata.fitmanage.domain.entity.News;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.NewsStatus;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.exception.DamilSoftException;
import demos.springdata.fitmanage.repository.NewsRepository;
import demos.springdata.fitmanage.service.NewsService;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, UserService userService, RoleService roleService) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.roleService = roleService;
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
        Set<RoleType> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        List<News> allTenantNews = newsRepository.findAllByTenantId(user.getTenant().getId());

        List<News> relevantNews = allTenantNews.stream()
                .filter(news -> isNewsRelevantForUser(news, user.getId(), roles))
                .toList();

        return relevantNews.stream()
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
        Set<String> targetRoles = request.getTargetRoles();

        if (request.getPublicationType() == PublicationType.TARGETED) {

            Set<RoleType> roleTypes = targetRoles.stream()
                    .map(String::toUpperCase)
                    .map(RoleType::valueOf)
                    .collect(Collectors.toSet());

            news.setTargetRoles(roleService.findByNameIn(roleTypes));
        }

        if (recipientsIds != null && !recipientsIds.isEmpty()) {
            news.setRecipientIds(recipientsIds);
        }

        if (request.getPublicationType() == PublicationType.TARGETED && (targetRoles == null && recipientsIds == null)){
            throw new DamilSoftException("Targeted news must specify at least one role or recipient ID.", HttpStatus.CONFLICT);
        }

        return news;
    }


    private boolean isNewsRelevantForUser(News news, Long userId, Set<RoleType> userRoles) {
        if (userRoles.contains(RoleType.ADMIN)) {
            return true;
        }

        if (news.getRecipientIds() != null && !news.getRecipientIds().isEmpty()) {
            return news.getRecipientIds().contains(userId);
        }

        if (news.getTargetRoles() == null || news.getTargetRoles().isEmpty()) {
            return true;
        }

        Set<RoleType> newsTargetRoles = news.getTargetRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return userRoles.stream().anyMatch(newsTargetRoles::contains);
    }

    private NewsResponse mapToDto(News news) {
        Set<Long> recipientIds = news.getRecipientIds();

        Set<RoleType> targetedRoles = news.getTargetRoles()
                .stream().map(Role::getName)
                .collect(Collectors.toSet());

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
