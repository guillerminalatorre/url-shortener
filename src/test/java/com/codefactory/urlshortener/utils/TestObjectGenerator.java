package com.codefactory.urlshortener.utils;

import com.codefactory.urlshortener.dto.UrlRequestDto;
import com.codefactory.urlshortener.entity.Url;
import com.codefactory.urlshortener.entity.UrlAccessLog;
import org.springframework.boot.SpringBootConfiguration;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootConfiguration
public class TestObjectGenerator {
    public static final String domain = "http://localhost:8080/";
    public static final String controllerPath = "";
    public static final String defaultPrefix = "A";
    public static final String email1 = "test@gmail.com";
    public static final String shortenedUrl1 = "vxm-2w"; // Real shortened URL for "http://example.com"
    public static final String shortenedUrl1Prefixed = defaultPrefix + "-" + shortenedUrl1;
    public static final String originalUrl1 = "http://example.com";

    public static final Url url1 = Url.builder()
            .id(shortenedUrl1Prefixed)
            .originalUrl(originalUrl1)
            .ownerEmail(email1).build();

    public static final UrlRequestDto urlRequestDto1 = new UrlRequestDto(originalUrl1, email1);

    public static final UrlRequestDto urlRequestDtoInvalidEmail = new UrlRequestDto(originalUrl1, "email1");

    public static final UrlRequestDto urlRequestDtoInvalidURL = new UrlRequestDto("originalUrl1", email1);

    public static final UrlAccessLog urlAccessLogUrl1 = UrlAccessLog.builder().url(url1).build();

    public static final UrlAccessLog urlAccessLog1 = UrlAccessLog.builder().date(LocalDateTime.now()).id(UUID.randomUUID().toString()).url(url1).build();
}
