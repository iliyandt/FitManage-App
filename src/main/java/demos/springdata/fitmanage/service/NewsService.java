package demos.springdata.fitmanage.service;

import demos.springdata.fitmanage.domain.dto.news.NewsRequest;
import demos.springdata.fitmanage.domain.dto.news.NewsResponse;

import java.util.List;

public interface NewsService {

    NewsResponse createNews(NewsRequest request);
    List<NewsResponse> getNewsForUser();
    NewsResponse delete(Long newsId);
    NewsResponse update(Long newsId, NewsRequest request);
}
