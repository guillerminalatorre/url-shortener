package com.codefactory.urlshortener.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Date;

@Data
@Entity
@Table(name = "url_access_logs")
public class UrlAccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)// TODO: use as ID the trace id from logging (not auto-generated)
    private String id;
    @CreationTimestamp
    private Date date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private Url urlId;
}
