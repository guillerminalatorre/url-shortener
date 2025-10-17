package com.codefactory.urlshortener.repository;

import com.codefactory.urlshortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, String>{
    Optional<Url> findByOriginalUrl(String originalUrl);
}
