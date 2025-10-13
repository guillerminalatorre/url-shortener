package com.codefactory.urlshortener.repository;

import com.codefactory.urlshortener.entity.UrlAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlAccessLogRepository extends JpaRepository<UrlAccessLog, String> {
}
