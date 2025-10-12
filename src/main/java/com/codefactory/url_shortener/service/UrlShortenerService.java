package com.codefactory.url_shortener.service;

import com.codefactory.url_shortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Url saveUrl(UrlRequestDto urlRequestDto) {
        log.info("message='Saving URL' originalUrl={} requestedBy={}", urlRequestDto.getOriginalUrl(), urlRequestDto.getOwnerEmail());

        // Shorten URL
        String shortenURL = generateShortUrl(urlRequestDto.getOriginalUrl());

        // Veryfy if URL already exists
        Optional<Url> existingUrl = urlRepository.findById(shortenURL); // TODO: Should inject the service to get access to the cached records

        if (existingUrl.isPresent()) {
            // If URL already exists, return existing shortened URL
            Url existingUrl = existingUrl.get();

            log.info("message='URL already exists' originalUrl={} shortenUrl={} owner={} requestedBy={}",
                    urlRequestDto.getOriginalUrl(), shortenURL, existingUrl.getOwnerEmail(), urlRequestDto.getOwnerEmail());

            return Url;
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
