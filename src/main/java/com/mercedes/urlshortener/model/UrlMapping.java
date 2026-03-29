package com.mercedes.urlshortener.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "url_mapping")
@Table(name = "url_mapping")
public class UrlMapping {

    private Long id;

    @Column(name = "full_url", nullable = false)
    private String fullUrl;

    @Column(name = "alias", nullable = false, unique = true)
    private String alias;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UrlMapping() {
    }

    public UrlMapping(String fullUrl, String alias) {
        this.fullUrl = fullUrl;
        this.alias = alias;
        this.createdAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UrlMapping{" +
                "id=" + id +
                ", fullUrl='" + fullUrl + '\'' +
                ", alias='" + alias + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
