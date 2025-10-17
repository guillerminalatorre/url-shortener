package com.codefactory.urlshortener.integration.service;

import com.codefactory.urlshortener.dto.UrlRequestDto;
import com.codefactory.urlshortener.entity.Url;
import com.codefactory.urlshortener.entity.UrlAccessLog;
import com.codefactory.urlshortener.integration.config.TestCacheConfig;
import com.codefactory.urlshortener.repository.UrlAccessLogRepository;
import com.codefactory.urlshortener.repository.UrlRepository;
import com.codefactory.urlshortener.service.UrlShortenerService;
import com.codefactory.urlshortener.shortener.Base32UrlHasher;
import com.codefactory.urlshortener.shortener.UrlHasherFactory;
import com.codefactory.urlshortener.utils.TestObjectGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(TestCacheConfig.class)
public class UrlHasherServiceIntegrationTest {
    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private Base32UrlHasher base32UrlHasher;

    @Autowired
    private UrlHasherFactory urlHasherFactory;

    @Autowired
    private UrlAccessLogRepository urlAccessLogRepository;

    @Test
    @Order(1)
    void whenSaveUrl_thenPersistsInDatabaseAndCache() throws Exception {
        // Arrange
        UrlRequestDto dto = TestObjectGenerator.urlRequestDto1;

        // Act
        Url saved = urlShortenerService.shortenUrlAndSave(dto);

        // Assert saved entity
        Assertions.assertEquals(TestObjectGenerator.shortenedUrl1Prefixed, saved.getId());
        Assertions.assertEquals(dto.getOwnerEmail(), saved.getOwnerEmail());
        Assertions.assertEquals(dto.getOriginalUrl(), saved.getOriginalUrl());

        // Assert persisted entity
        Optional<Url> fromDb = urlRepository.findById(saved.getId());
        Assertions.assertTrue(fromDb.isPresent());
        Assertions.assertEquals(saved.getId(), fromDb.get().getId());
        Assertions.assertEquals(saved.getOriginalUrl(), fromDb.get().getOriginalUrl());
        Assertions.assertEquals(saved.getOwnerEmail(), fromDb.get().getOwnerEmail());

        // Assert cached entity
        Url cached = cacheManager.getCache("urls").get(saved.getId(), Url.class);
        Assertions.assertNotNull(cached);
        Assertions.assertEquals(saved.getOriginalUrl(), cached.getOriginalUrl());
    }

    @Test
    @Order(2)
    void whenSaveUrl_thenFindsItDB() throws Exception {
        // Arrange
        UrlRequestDto dto = TestObjectGenerator.urlRequestDto1;

        // Act
        Url saved = urlShortenerService.shortenUrlAndSave(dto);

        // Assert saved entity
        Assertions.assertEquals(TestObjectGenerator.shortenedUrl1Prefixed, saved.getId());
        Assertions.assertEquals(dto.getOwnerEmail(), saved.getOwnerEmail());
        Assertions.assertEquals(dto.getOriginalUrl(), saved.getOriginalUrl());

        // Assert persisted entity
        Optional<Url> fromDb = urlRepository.findById(saved.getId());
        Assertions.assertTrue(fromDb.isPresent());
        Assertions.assertEquals(saved.getId(), fromDb.get().getId());
        Assertions.assertEquals(saved.getOriginalUrl(), fromDb.get().getOriginalUrl());
        Assertions.assertEquals(saved.getOwnerEmail(), fromDb.get().getOwnerEmail());
    }

    @Test
    @Order(3)
    void whenGetUrl_thenFindsItInCache() {
        // Act
        Optional<Url> found = urlShortenerService.getUrlAndSaveUrlAccessLog(TestObjectGenerator.shortenedUrl1Prefixed);

        // Assert found entity in Cache
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(TestObjectGenerator.shortenedUrl1Prefixed, found.get().getId());
        Assertions.assertEquals(TestObjectGenerator.originalUrl1, found.get().getOriginalUrl());
        Assertions.assertEquals(TestObjectGenerator.email1, found.get().getOwnerEmail());

        // Assert saved entity in UrlAccessLog
        Optional<UrlAccessLog> savedLog = urlAccessLogRepository.findAll().stream().findFirst();
        Assertions.assertTrue(savedLog.isPresent());
        Assertions.assertEquals(TestObjectGenerator.shortenedUrl1Prefixed, savedLog.get().getUrl().getId());
    }

    @Test
    @Order(4)
    void whenGetNonExistingUrl_thenReturnsEmpty() {
        // Act
        Optional<Url> found = urlShortenerService.getUrlAndSaveUrlAccessLog("nonExistingId");

        // Assert not found entity
        Assertions.assertTrue(found.isEmpty());
    }
}
