package com.codefactory.urlshortener.unit.service;

import com.codefactory.urlshortener.entity.Url;
import com.codefactory.urlshortener.repository.UrlRepository;
import com.codefactory.urlshortener.utils.TestObjectGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Test
    public void testSaveUrl_NewUrl() {
        // Arrange
        when(urlRepository.findById(TestObjectGenerator.shortenUrl1)).thenReturn(Optional.empty());
        when(urlRepository.save(TestObjectGenerator.url1)).thenReturn(TestObjectGenerator.url1);

        // Act
        Url savedUrl = urlShortenerService.saveUrl(TestObjectGenerator.urlRequestDto1);

        // Assert
        Assertions.assertEquals(TestObjectGenerator.shortenUrl1, savedUrl.getId());
        Assertions.assertEquals(TestObjectGenerator.originalUrl1, savedUrl.getOriginalUrl());
        Assertions.assertEquals(TestObjectGenerator.email1, savedUrl.getOwnerEmail());
    }

    @Test
    public void testSaveUrl_ExistingUrl() {
        // Arrange
        when(urlRepository.findById(TestObjectGenerator.shortenUrl1)).thenReturn(Optional.of(TestObjectGenerator.url1));

        // Act
        Url existingUrl = urlShortenerService.saveUrl(TestObjectGenerator.urlRequestDto1);

        // Assert
        Assertions.assertEquals(TestObjectGenerator.shortenUrl1, existingUrl.getId());
        Assertions.assertEquals(TestObjectGenerator.originalUrl1, existingUrl.getOriginalUrl());
        Assertions.assertEquals(TestObjectGenerator.email1, existingUrl.getOwnerEmail());
    }

    @Test
    public void testSaveUrl_DBError_when_saving() {
        // Arrange
        when(urlRepository.findById(TestObjectGenerator.shortenUrl1)).thenReturn(Optional.empty());
        when(urlRepository.save(TestObjectGenerator.url1)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            urlShortenerService.saveUrl(TestObjectGenerator.urlRequestDto1);
        });

        Assertions.assertEquals("Database error", exception.getMessage());
    }

    @Test
    public void testSaveUrl_DBError_when_fetching() {
        // Arrange
        when(urlRepository.findById(TestObjectGenerator.shortenUrl1)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            urlShortenerService.saveUrl(TestObjectGenerator.urlRequestDto1);
        });

        Assertions.assertEquals("Database error", exception.getMessage());
    }

    @Test
    public void testGetOriginalUrl_Found() {
        // Arrange
        when(urlRepository.findById(TestObjectGenerator.shortenUrl1)).thenReturn(Optional.of(TestObjectGenerator.url1));

        // Act
        Optional<Url> foundUrl = urlShortenerService.getOriginalUrl(TestObjectGenerator.shortenUrl1);

        // Assert
        Assertions.assertTrue(foundUrl.isPresent());
        Assertions.assertEquals(TestObjectGenerator.shortenUrl1, foundUrl.get().getId());
        Assertions.assertEquals(TestObjectGenerator.originalUrl1, foundUrl.get().getOriginalUrl());
        Assertions.assertEquals(TestObjectGenerator.email1, foundUrl.get().getOwnerEmail());
    }
}