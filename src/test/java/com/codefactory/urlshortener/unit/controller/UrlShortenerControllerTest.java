package com.codefactory.urlshortener.unit.controller;

import com.codefactory.urlshortener.dto.UrlRequestDto;
import com.codefactory.urlshortener.dto.UrlResponseDto;
import com.codefactory.urlshortener.unit.service.UrlShortenerService;
import com.codefactory.urlshortener.utils.TestObjectGenerator;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerControllerTest {
    @Mock
    private UrlShortenerService urlShortenerService;

    @InjectMocks
    private UrlShortenerController urlShortenerController;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(urlShortenerController, "domain", TestObjectGenerator.domain);
    }

    @Test
    public void testCreateShortUrl() {
        // Arrange
        UrlRequestDto urlRequestDto = TestObjectGenerator.urlRequestDto1;
        when(urlShortenerService.saveUrl(urlRequestDto)).thenReturn(TestObjectGenerator.url1);

        // Act
        ResponseEntity<UrlResponseDto> response = urlShortenerController.shortenUrl(TestObjectGenerator.urlRequestDto1);

        // Assert
        Assertions.assertEquals(urlRequestDto.getOriginalUrl(), response.getBody().getOriginalUrl());
        Assertions.assertEquals(urlRequestDto.getOwnerEmail(), response.getBody().getOwnerEmail());
        Assertions.assertEquals(TestObjectGenerator.domain + TestObjectGenerator.shortenUrl1, response.getBody().getShortenedUrl());
    }

    @Test
    public void testCreateShortUrl_InvalidEmail() {
        // Arrange
        UrlRequestDto urlRequestDto = TestObjectGenerator.urlRequestDtoInvalidEmail;

        // Act & Assert
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            urlShortenerController.shortenUrl(urlRequestDto);
        });
    }
}
