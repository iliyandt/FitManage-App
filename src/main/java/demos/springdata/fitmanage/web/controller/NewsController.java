package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.news.NewsRequest;
import demos.springdata.fitmanage.domain.dto.news.NewsResponse;
import demos.springdata.fitmanage.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NewsResponse>> createNews(@RequestBody NewsRequest request) {
        NewsResponse response = newsService.createNews(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<NewsResponse>>> getNews() {
        List<NewsResponse> news = newsService.getNewsForUser();
        return ResponseEntity.ok(ApiResponse.success(news));
    }
}
