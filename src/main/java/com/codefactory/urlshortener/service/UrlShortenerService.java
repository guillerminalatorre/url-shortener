package com.codefactory.urlshortener.service;

import com.codefactory.urlshortener.dto.UrlRequestDto;
import com.codefactory.urlshortener.entity.Url;
import com.codefactory.urlshortener.repository.UrlRepository;
import com.codefactory.urlshortener.utils.Shortener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UrlShortenerService {

    @Autowired
    private UrlRepository urlRepository;

    public Url saveUrl(UrlRequestDto urlRequestDto) {
        log.info("message='Saving URL' originalUrl={} requestedBy={}", urlRequestDto.getOriginalUrl(), urlRequestDto.getOwnerEmail());

        // Shorten URL
        String shortenURL = Shortener.shortenUrl(urlRequestDto.getOriginalUrl());

        // Verify if URL already exists
        Optional<Url> existingUrlOptional = urlRepository.findById(shortenURL); // TODO: Should inject the service to get access to the cached records

        if (existingUrlOptional.isPresent()) {
            // If URL already exists, return existing shortened URL
            Url existingUrl = existingUrlOptional.get();

            log.info("message='URL already exists' originalUrl={} shortenUrl={} owner={} requestedBy={}",
                    urlRequestDto.getOriginalUrl(), shortenURL, existingUrl.getOwnerEmail(), urlRequestDto.getOwnerEmail());

            return existingUrl;
        } else {
            // If URL does not exist, save new URL
            Url url = Url.builder()
                    .id(shortenURL)
                    .originalUrl(urlRequestDto.getOriginalUrl())
                    .ownerEmail(urlRequestDto.getOwnerEmail())
                    .build();

            try {
                Url savedUrl = urlRepository.save(url);
                log.info("message='URL saved successfully' originalUrl={} shortenUrl={} requestedBy={}",
                        savedUrl.getOriginalUrl(), savedUrl.getId(), savedUrl.getOwnerEmail());

                return savedUrl;
            } catch (Exception e) {
                log.error("message='Error saving URL' originalUrl={} shortenUrl={} requestedBy={} error={}",
                        urlRequestDto.getOriginalUrl(), shortenURL, urlRequestDto.getOwnerEmail(), e.getMessage());
                throw e;
            }
        }
    }

    // TODO: Cache the generated URLs objects to improve performance
    public Optional<Url> getOriginalUrl(String originalUrl) {
        return urlRepository.findById(originalUrl);
    }
}
