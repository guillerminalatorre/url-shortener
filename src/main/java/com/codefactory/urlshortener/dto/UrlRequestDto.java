package com.codefactory.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestDto {
    @Schema(
            description = "Original URL to shorten",
            example = "https://www.ejemplo.com"
    )
    @URL(message = "Original URL must be a valid URL")
    @NotBlank(message = "Original URL must not be blank")
    private String originalUrl;

    @Email(message = "Owner email must be a valid email address")
    @NotBlank(message = "Owner email must not be blank")
    private String ownerEmail;
}
