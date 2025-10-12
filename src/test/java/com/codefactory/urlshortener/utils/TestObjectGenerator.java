package com.codefactory.urlshortener.utils;

import com.codefactory.urlshortener.dto.UrlRequestDto;
import com.codefactory.urlshortener.entity.Url;

public class TestObjectGenerator {
    public static final String email1 = "test@gmail.com";
    public static final String shortenUrl1 = "vxm-2w"; // Real shortened URL for "http://example.com"
    public static final String originalUrl1 = "http://example.com";

    public static final Url url1 = Url.builder()
            .id(shortenUrl1)
            .originalUrl(originalUrl1)
            .ownerEmail(email1).build();

    public static final UrlRequestDto urlRequestDto1 = new UrlRequestDto(originalUrl1, email1);

    public static final UrlRequestDto urlRequestDtoInvalidEmail = new UrlRequestDto(originalUrl1, "email1");

    public static final UrlRequestDto urlRequestDtoInvalidURL = new UrlRequestDto("originalUrl1", email1);

    public static final UrlRequestDto urlRequestDtoInjectScript = new UrlRequestDto("<script>alert('XSS')</script>", email1);
}
