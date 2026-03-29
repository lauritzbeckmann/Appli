package com.mercedes.urlshortener.repository;

import com.mercedes.urlshortener.model.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    @Query("SELECT c FROM click_event c WHERE c.urlId = ?1")
    List<ClickEvent> findByUrlId(Long urlId);

    @Query("SELECT c FROM click_event c WHERE c.urlId = ?1 AND c.clickedAt >= ?2 AND c.clickedAt <= ?3")
    List<ClickEvent> findByUrlIdAndDateRange(Long urlId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(c) FROM click_event c WHERE c.urlId = ?1")
    long countByUrlId(Long urlId);

    @Query("SELECT COUNT(c) FROM click_event c WHERE c.urlId = ?1 AND c.clickedAt >= ?2 AND c.clickedAt <= ?3")
    long countByUrlIdAndDateRange(Long urlId, LocalDateTime startDate, LocalDateTime endDate);
}
