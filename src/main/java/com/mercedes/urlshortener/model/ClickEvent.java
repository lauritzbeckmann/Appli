package com.mercedes.urlshortener.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "click_event")
@Table(name = "click_event")
public class ClickEvent {

    private Long id;

    @Column(name = "url_id", nullable = false)
    private Long urlId;

    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;

    public ClickEvent() {
    }

    public ClickEvent(Long urlId) {
        this.urlId = urlId;
        this.clickedAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    public LocalDateTime getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }

    @Override
    public String toString() {
        return "ClickEvent{" +
                "id=" + id +
                ", urlId=" + urlId +
                ", clickedAt=" + clickedAt +
                '}';
    }
}
