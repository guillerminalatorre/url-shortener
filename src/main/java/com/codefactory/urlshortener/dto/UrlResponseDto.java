package com.codefactory.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponseDto {
    private String originalUrl;
    private String shortenedUrl;
    private String ownerEmail;
}
