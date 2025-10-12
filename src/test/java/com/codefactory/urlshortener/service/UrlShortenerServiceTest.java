package com.codefactory.urlshortener.service;

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
}