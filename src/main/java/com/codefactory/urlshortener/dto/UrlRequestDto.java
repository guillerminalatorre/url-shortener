package com.codefactory.urlshortener.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
public class UrlRequestDto {
    @URL
    @NotBlank
    private String originalUrl;

    @Email
    @NotBlank
    private String ownerEmail;
}
