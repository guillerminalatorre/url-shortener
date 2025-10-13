package com.codefactory.urlshortener.controller;

import com.codefactory.urlshortener.service.UrlShortenerService;
import com.codefactory.urlshortener.dto.UrlRequestDto;
import com.codefactory.urlshortener.dto.UrlResponseDto;
import com.codefactory.urlshortener.entity.Url;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("")
@Slf4j
@Tag(name = "URL Shortener", description = "API for shortening URLs")
public class UrlShortenerController {
    @Autowired
    private UrlShortenerService urlShortenerService;

    @Value("${app.url.base}")
    private String domain;

    @Operation(summary = "Shorten a URL", description = "Creates a shortened URL for the given original URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "URL shortened successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<UrlResponseDto> shortenUrl(@Valid @RequestBody UrlRequestDto urlRequestDto) throws Exception{
        log.info("message='Received request to shorten URL' originalUrl={} requestedBy={}",
                urlRequestDto.getOriginalUrl(), urlRequestDto.getOwnerEmail());
        Url url = urlShortenerService.shortenUrlAndSave(urlRequestDto);
        String shortenedUrl = domain + url.getId();
        return ResponseEntity.created(URI.create(shortenedUrl))
                .body(UrlResponseDto.builder()
                        .shortenedUrl(shortenedUrl)
                        .originalUrl(url.getOriginalUrl())
                        .ownerEmail(url.getOwnerEmail())
                        .build());
    }

    @Operation(summary = "Redirect to original URL", description = "Redirects to the original URL based on the shortened URL ID. " +
            "Paste the request URL in your browser to test the redirection, otherwise it will fail due CORS policy.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "Redirecting to original URL"),
        @ApiResponse(responseCode = "404", description = "Shortened URL not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String id) {
        log.info("message='Received request to redirect to original URL' shortenedUrl={}", id);
        Optional<Url> url = urlShortenerService.getUrlAndSaveUrlAccessLog(id);
        return url.isPresent() ? ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(url.get().getOriginalUrl()))
                    .build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
