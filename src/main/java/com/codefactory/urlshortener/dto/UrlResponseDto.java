package com.codefactory.urlshortener.dto;

import com.codefactory.urlshortener.entity.Url;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlResponseDto {
    private String originalUrl;
    private String shortenedUrl;
    private String ownerEmail;
}
