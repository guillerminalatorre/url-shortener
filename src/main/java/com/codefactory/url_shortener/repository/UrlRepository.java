package com.codefactory.url_shortener.repository;

import com.codefactory.url_shortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, String>{
}
