package demos.springdata.fitmanage.service.impl;

import demos.springdata.fitmanage.domain.dto.news.NewsRequest;
import demos.springdata.fitmanage.domain.dto.news.NewsResponse;
import demos.springdata.fitmanage.domain.entity.News;
import demos.springdata.fitmanage.domain.entity.Role;
import demos.springdata.fitmanage.domain.entity.Tenant;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.domain.enums.NewsStatus;
import demos.springdata.fitmanage.domain.enums.PublicationType;
import demos.springdata.fitmanage.domain.enums.RoleType;
import demos.springdata.fitmanage.repository.NewsRepository;
import demos.springdata.fitmanage.service.RoleService;
import demos.springdata.fitmanage.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsServiceImplUTest {
    @Mock
    private NewsRepository newsRepository;
    @Mock
    private UserService userService;
    @Mock
    private RoleService roleService;

    @InjectMocks
    private NewsServiceImpl newsServiceImpl;

    private User adminUser;
    private User normalUser;
    private Tenant tenant;
    private Role adminRole;
    private Role userRole;
    private Role trainerRole;

    @BeforeEach
    void setUp() {

        tenant = new Tenant();
        tenant.setId(UUID.randomUUID());

        adminRole = new Role(RoleType.ADMIN);
        userRole = new Role(RoleType.MEMBER);
        trainerRole = new Role(RoleType.STAFF);

        adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setTenant(tenant);
        adminUser.setRoles(Set.of(adminRole));

        normalUser = new User();
        normalUser.setId(UUID.randomUUID());
        normalUser.setTenant(tenant);
        normalUser.setRoles(Set.of(userRole));
    }



    @Test
    void createNews_ShouldCreateGeneralNews_WhenValidRequest() {

        when(userService.getCurrentUser()).thenReturn(adminUser);

        NewsRequest request = new NewsRequest();
        request.setTitle("General Announcement");
        request.setContent("Hello World");
        request.setPublicationType(PublicationType.ALL);

        when(newsRepository.save(any(News.class))).thenAnswer(i -> {
            News n = i.getArgument(0);
            n.setId(UUID.randomUUID());
            return n;
        });

        NewsResponse response = newsServiceImpl.createNews(request);

        assertNotNull(response);
        assertEquals("General Announcement", response.getTitle());
        assertEquals(PublicationType.ALL, response.getPublicationType());
        verify(newsRepository).save(any(News.class));
    }

    @Test
    void getNewsForUser_ShouldReturnAllNews_WhenUserIsAdmin() {

        when(userService.getCurrentUser()).thenReturn(adminUser);

        News news1 = createNewsMock(PublicationType.ALL, null);
        News news2 = createNewsMock(PublicationType.TARGETED, Set.of(trainerRole));

        when(newsRepository.findAllByTenantId(tenant.getId())).thenReturn(List.of(news1, news2));

        List<NewsResponse> result = newsServiceImpl.getNewsForUser();

        assertEquals(2, result.size(), "Admin should see all news regardless of target");
    }

    @Test
    void getNewsForUser_ShouldFilterNews_WhenUserIsNormalUser() {

        when(userService.getCurrentUser()).thenReturn(normalUser);

        News generalNews = createNewsMock(PublicationType.ALL, null);

        News userNews = createNewsMock(PublicationType.TARGETED, Set.of(userRole));

        News trainerNews = createNewsMock(PublicationType.TARGETED, Set.of(trainerRole));

        News specificNews = createNewsMock(PublicationType.TARGETED, null);
        specificNews.setRecipientIds(Set.of(normalUser.getId()));

        when(newsRepository.findAllByTenantId(tenant.getId()))
                .thenReturn(List.of(generalNews, userNews, trainerNews, specificNews));

        List<NewsResponse> result = newsServiceImpl.getNewsForUser();

        assertEquals(3, result.size());
        assertTrue(result.stream().noneMatch(n -> n.getTargetRoles().contains(RoleType.STAFF)));
    }

    @Test
    void getNewsForUser_ShouldReturnGeneral_WhenTargetRolesIsNull() {
        when(userService.getCurrentUser()).thenReturn(normalUser);

        News weirdNews = createNewsMock(PublicationType.TARGETED, null);

        when(newsRepository.findAllByTenantId(tenant.getId())).thenReturn(List.of(weirdNews));

        List<NewsResponse> result = newsServiceImpl.getNewsForUser();
        assertEquals(1, result.size());
    }


    @Test
    void update_ShouldUpdateFields_WhenNewsExists() {
        UUID newsId = UUID.randomUUID();
        News existingNews = createNewsMock(PublicationType.ALL, null);
        existingNews.setId(newsId);
        existingNews.setTitle("Old Title");

        when(newsRepository.getNewsById(newsId)).thenReturn(existingNews);

        NewsRequest updateRequest = new NewsRequest();
        updateRequest.setTitle("New Title");
        updateRequest.setContent("New Content");
        updateRequest.setPublicationType(PublicationType.ALL);

        NewsResponse response = newsServiceImpl.update(newsId, updateRequest);

        assertEquals("New Title", response.getTitle());
        verify(newsRepository).getNewsById(newsId);
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {

        UUID newsId = UUID.randomUUID();
        News news = new News();
        news.setId(newsId);
        news.setAuthor(adminUser);

        when(newsRepository.getNewsById(newsId)).thenReturn(news);


        newsServiceImpl.delete(newsId);


        verify(newsRepository).delete(news);
    }



    private News createNewsMock(PublicationType type, Set<Role> targetRoles) {
        News news = new News();
        news.setId(UUID.randomUUID());
        news.setTitle("Test News");
        news.setContent("Content");
        news.setAuthor(adminUser);
        news.setPublishedAt(Instant.now());
        news.setStatus(NewsStatus.PUBLISHED);
        news.setPublicationType(type);
        news.setTargetRoles(targetRoles != null ? targetRoles : Collections.emptySet());
        return news;
    }
}
