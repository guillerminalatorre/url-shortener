package com.codefactory.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "urls")
public class Url implements Serializable {
    @Id
    private String id;
    @Column(nullable = false, length = 2048, unique = true)
    private String originalUrl;
    @Column(nullable = false, length = 2048)
    private String ownerEmail;
    @CreationTimestamp
    private LocalDateTime creationDate;
}
