package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.news.NewsRequest;
import demos.springdata.fitmanage.domain.dto.news.NewsResponse;

import java.util.List;
import java.util.UUID;

public interface NewsService {

    NewsResponse createNews(NewsRequest request);
    List<NewsResponse> getNewsForUser();
    NewsResponse delete(UUID newsId);
    NewsResponse update(UUID newsId, NewsRequest request);
}
