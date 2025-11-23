package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.news.NewsRequest;
import demos.springdata.fitmanage.domain.dto.news.NewsResponse;
import demos.springdata.fitmanage.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/news")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'MEMBER')")
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



    @PutMapping("/{newsId}")
    public ResponseEntity<ApiResponse<NewsResponse>> editNews(@PathVariable UUID newsId, @RequestBody NewsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(newsService.update(newsId, request)));
    }


    @DeleteMapping("/{newsId}")
    public ResponseEntity<ApiResponse<NewsResponse>> deleteNews(@PathVariable UUID newsId) {
        return ResponseEntity.ok(ApiResponse.success(newsService.delete(newsId)));
    }
}
