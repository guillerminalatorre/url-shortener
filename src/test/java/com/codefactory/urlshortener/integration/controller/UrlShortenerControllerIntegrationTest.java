package com.codefactory.urlshortener.integration.controller;

import com.codefactory.urlshortener.controller.UrlShortenerController;
import com.codefactory.urlshortener.dto.UrlRequestDto;
import com.codefactory.urlshortener.entity.Url;
import com.codefactory.urlshortener.service.UrlShortenerService;
import com.codefactory.urlshortener.utils.TestObjectGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UrlShortenerController.class)
class UrlShortenerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlShortenerService urlShortenerService;

    @InjectMocks
    private UrlShortenerController urlShortenerController;

    @BeforeEach
    void setUp() throws Exception {
        ReflectionTestUtils.setField(urlShortenerController, "domain", TestObjectGenerator.domain);
    }

    @Test
    void whenValidUrl_thenReturnsShortenedUrl() throws Exception {
        // Arrange
        UrlRequestDto dto = TestObjectGenerator.urlRequestDto1;
        Url url = TestObjectGenerator.url1;
        when(urlShortenerService.shortenUrlAndSave(dto)).thenReturn(url);

        // Act & Assert
        mockMvc.perform(post(TestObjectGenerator.controllerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortenedUrl").value(TestObjectGenerator.domain + TestObjectGenerator.shortenedUrl1))
                .andExpect(jsonPath("$.originalUrl").value(TestObjectGenerator.originalUrl1))
                .andExpect(jsonPath("$.ownerEmail").value(TestObjectGenerator.email1));
    }

    @Test
    void whenInvalidEmail_thenReturnsBadRequest() throws Exception {
        // Arrange
        UrlRequestDto dto = TestObjectGenerator.urlRequestDtoInvalidEmail;

        // Act & Assert
        mockMvc.perform(post(TestObjectGenerator.controllerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Owner email must be a valid email address"));
    }

    @Test
    void whenInvalidUrl_thenReturnsBadRequest() throws Exception {
        // Arrange
        UrlRequestDto dto = TestObjectGenerator.urlRequestDtoInvalidURL;

        // Act & Assert
        mockMvc.perform(post(TestObjectGenerator.controllerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Original URL must be a valid URL"));
    }

    @Test
    void whenInternalError_thenReturnsServerError() throws Exception {
        // Arrange
        UrlRequestDto dto = TestObjectGenerator.urlRequestDto1;
        when(urlShortenerService.shortenUrlAndSave(any(UrlRequestDto.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post(TestObjectGenerator.controllerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Database error")));
    }

    @Test
    void whenRedirectToOriginalUrl_thenReturnsFound() throws Exception {
        // Arrange
        String shortenedUrl = TestObjectGenerator.shortenedUrl1;
        Url url = TestObjectGenerator.url1;
        when(urlShortenerService.getUrlAndSaveUrlAccessLog(shortenedUrl)).thenReturn(Optional.of(url));

        // Act & Assert
        mockMvc.perform(get("/" + shortenedUrl))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", TestObjectGenerator.originalUrl1));
    }

    @Test
    void whenRedirectToOriginalUrl_thenReturnsNotFound() throws Exception {
        // Arrange
        String shortenedUrl = TestObjectGenerator.shortenedUrl1;
        Url url = TestObjectGenerator.url1;
        when(urlShortenerService.getUrlAndSaveUrlAccessLog(shortenedUrl)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/" + shortenedUrl))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}