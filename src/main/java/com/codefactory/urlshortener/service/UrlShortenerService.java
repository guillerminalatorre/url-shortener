package com.codefactory.urlshortener.service;

import com.codefactory.urlshortener.dto.UrlRequestDto;
import com.codefactory.urlshortener.entity.Url;
import com.codefactory.urlshortener.entity.UrlAccessLog;
import com.codefactory.urlshortener.repository.UrlAccessLogRepository;
import com.codefactory.urlshortener.repository.UrlRepository;
import com.codefactory.urlshortener.shortener.UrlHasher;
import com.codefactory.urlshortener.shortener.UrlHasherFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = "urls")
public class UrlShortenerService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlAccessLogRepository urlAccessLogRepository;

    @Autowired
    @Lazy
    private UrlShortenerService self;

    @Value("${app.url.prefixes.default}")
    private String defaultPrefix;

    @Autowired
    private UrlHasherFactory urlHasherFactory;

    @CachePut(value = "urls", key = "#result.id")
    public Url shortenUrlAndSave(UrlRequestDto urlRequestDto) throws Exception {
        log.info("message='Saving URL' originalUrl={} requestedBy={}", urlRequestDto.getOriginalUrl(), urlRequestDto.getOwnerEmail());

        // Verify if URL already exists
        Optional<Url> existingUrlOptional = self.getUrlByOriginalUrl(urlRequestDto.getOriginalUrl());

        if (existingUrlOptional.isPresent()) {
            // If URL already exists, return existing shortened URL
            Url existingUrl = existingUrlOptional.get();

            log.info("message='URL already exists' originalUrl={} shortenedUrl={} owner={} requestedBy={}",
                    urlRequestDto.getOriginalUrl(), existingUrl.getId(), existingUrl.getOwnerEmail(), urlRequestDto.getOwnerEmail());

            return existingUrl;
        } else {
            // Shorten URL
            String shortenedURL = defaultPrefix + "-" + urlHasherFactory.getByPrefix(defaultPrefix).hash(urlRequestDto.getOriginalUrl());

            // If URL does not exist, save new URL in DB and cache
            Url url = Url.builder()
                    .id(shortenedURL)
                    .originalUrl(urlRequestDto.getOriginalUrl())
                    .ownerEmail(urlRequestDto.getOwnerEmail())
                    .build();

            try {
                Url savedUrl = urlRepository.save(url);
                log.info("message='URL saved successfully' originalUrl={} shortenedUrl={} requestedBy={}",
                        savedUrl.getOriginalUrl(), savedUrl.getId(), savedUrl.getOwnerEmail());

                return savedUrl;
            } catch (Exception e) {
                log.error("message='Error saving URL' originalUrl={} shortenedUrl={} requestedBy={} error={}",
                        urlRequestDto.getOriginalUrl(), shortenedURL, urlRequestDto.getOwnerEmail(), e.getMessage());
                throw e;
            }
        }
    }

    public Optional<Url> getUrlAndSaveUrlAccessLog(String shortenedUrl) {
        log.info("message='Retrieving URL and saving access log' shortenedUrl={}", shortenedUrl);
        Optional<Url> urlOptional = self.getUrlByShortenedUrl(shortenedUrl);
        if (urlOptional.isPresent()) {
            try {
                urlAccessLogRepository.save(UrlAccessLog.builder().url(urlOptional.get()).build());
            } catch (Exception e) {
                log.error("message='Error saving UrlAccessLog' originalUrl={} shortenedUrl={} date={} error={}",
                        urlOptional.get().getOriginalUrl(), shortenedUrl, LocalDateTime.now(), e.getMessage());
            }
        }
        return urlOptional;
    }

    @Cacheable(value = "urls", key = "#shortenedUrl")
    public Optional<Url> getUrlByShortenedUrl(String shortenedUrl) {
        // will check the cache first, if not found, will query the DB
        log.info("message='Fetching URL by id from database' shortenedUrl={}", shortenedUrl);
        return urlRepository.findById(shortenedUrl);
    }

    public Optional<Url> getUrlByOriginalUrl(String originalUrl) {
        log.info("message='Fetching URL by originalUrl from database' originalUrl={}", originalUrl);
        return urlRepository.findByOriginalUrl(originalUrl);
    }
}
