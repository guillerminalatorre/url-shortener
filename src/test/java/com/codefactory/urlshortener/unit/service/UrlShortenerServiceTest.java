package com.codefactory.urlshortener.unit.service;

import com.codefactory.urlshortener.entity.Url;
import com.codefactory.urlshortener.repository.UrlAccessLogRepository;
import com.codefactory.urlshortener.repository.UrlRepository;
import com.codefactory.urlshortener.service.UrlShortenerService;
import com.codefactory.urlshortener.utils.TestObjectGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlAccessLogRepository urlAccessLogRepository;

    private UrlShortenerService selfMock;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        selfMock = mock(UrlShortenerService.class);
        ReflectionTestUtils.setField(urlShortenerService, "self", selfMock);
    }


    @Test
    public void testSaveUrl_NewUrl() throws Exception {
        // Arrange
        when(selfMock.getUrlByShortenedUrl(TestObjectGenerator.shortenedUrl1))
                .thenReturn(Optional.empty());
        when(urlRepository.save(TestObjectGenerator.url1)).thenReturn(TestObjectGenerator.url1);

        // Act
        Url savedUrl = urlShortenerService.shortenUrlAndSave(TestObjectGenerator.urlRequestDto1);

        // Assert
        Assertions.assertEquals(TestObjectGenerator.shortenedUrl1, savedUrl.getId());
        Assertions.assertEquals(TestObjectGenerator.originalUrl1, savedUrl.getOriginalUrl());
        Assertions.assertEquals(TestObjectGenerator.email1, savedUrl.getOwnerEmail());
    }

    @Test
    public void testSaveUrl_ExistingUrl() throws Exception {
        // Arrange
        when(selfMock.getUrlByShortenedUrl(TestObjectGenerator.shortenedUrl1)).thenReturn(Optional.of(TestObjectGenerator.url1));

        // Act
        Url existingUrl = urlShortenerService.shortenUrlAndSave(TestObjectGenerator.urlRequestDto1);

        // Assert

        Assertions.assertEquals(TestObjectGenerator.shortenedUrl1, existingUrl.getId());
        Assertions.assertEquals(TestObjectGenerator.originalUrl1, existingUrl.getOriginalUrl());
        Assertions.assertEquals(TestObjectGenerator.email1, existingUrl.getOwnerEmail());
    }

    @Test
    public void testSaveUrl_DBError_when_saving() throws Exception {
        // Arrange
        when(selfMock.getUrlByShortenedUrl(TestObjectGenerator.shortenedUrl1)).thenReturn(Optional.empty());
        when(urlRepository.save(TestObjectGenerator.url1)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            urlShortenerService.shortenUrlAndSave(TestObjectGenerator.urlRequestDto1);
        });

        Assertions.assertEquals("Database error", exception.getMessage());
    }

    @Test
    public void testSaveUrl_DBError_when_fetching() {
        // Arrange
        when(selfMock.getUrlByShortenedUrl(TestObjectGenerator.shortenedUrl1)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            urlShortenerService.shortenUrlAndSave(TestObjectGenerator.urlRequestDto1);
        });

        Assertions.assertEquals("Database error", exception.getMessage());
    }

    @Test
    public void testGetOriginalUrl_Found() {
        // Arrange
        when(selfMock.getUrlByShortenedUrl(TestObjectGenerator.shortenedUrl1)).thenReturn(Optional.of(TestObjectGenerator.url1));
        when(urlAccessLogRepository.save(TestObjectGenerator.urlAccessLogUrl1)).thenReturn(TestObjectGenerator.urlAccessLog1);

        // Act
        Optional<Url> foundUrl = urlShortenerService.getUrlAndSaveUrlAccessLog(TestObjectGenerator.shortenedUrl1);

        // Assert
        Assertions.assertTrue(foundUrl.isPresent());
        Assertions.assertEquals(TestObjectGenerator.shortenedUrl1, foundUrl.get().getId());
        Assertions.assertEquals(TestObjectGenerator.originalUrl1, foundUrl.get().getOriginalUrl());
        Assertions.assertEquals(TestObjectGenerator.email1, foundUrl.get().getOwnerEmail());
    }

    @Test
    public void testGetOriginalUrl_Found_DBError_when_savingLog() {
        // Arrange
        when(selfMock.getUrlByShortenedUrl(TestObjectGenerator.shortenedUrl1)).thenReturn(Optional.of(TestObjectGenerator.url1));
        when(urlAccessLogRepository.save(TestObjectGenerator.urlAccessLogUrl1)).thenThrow(new RuntimeException("Database error"));

        // Act
        Optional<Url> foundUrl = urlShortenerService.getUrlAndSaveUrlAccessLog(TestObjectGenerator.shortenedUrl1);

        // Assert
        Assertions.assertTrue(foundUrl.isPresent());
        Assertions.assertEquals(TestObjectGenerator.shortenedUrl1, foundUrl.get().getId());
        Assertions.assertEquals(TestObjectGenerator.originalUrl1, foundUrl.get().getOriginalUrl());
        Assertions.assertEquals(TestObjectGenerator.email1, foundUrl.get().getOwnerEmail());
    }

    @Test
    public void testGetOriginalUrl_NotFound() {
        // Arrange
        when(selfMock.getUrlByShortenedUrl(TestObjectGenerator.shortenedUrl1)).thenReturn(Optional.empty());

        // Act
        Optional<Url> foundUrl = urlShortenerService.getUrlAndSaveUrlAccessLog(TestObjectGenerator.shortenedUrl1);

        // Assert
        Assertions.assertTrue(foundUrl.isEmpty());
    }
}