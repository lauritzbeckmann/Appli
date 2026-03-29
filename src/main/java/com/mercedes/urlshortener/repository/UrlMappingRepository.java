package com.mercedes.urlshortener.repository;

import com.mercedes.urlshortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    @Query("SELECT u FROM url_mapping u WHERE u.fullUrl = ?1")
    List<UrlMapping> findByFullUrl(String fullUrl);

    @Query("SELECT u FROM url_mapping u WHERE u.alias = ?1")
    Optional<UrlMapping> findByAlias(String alias);

    boolean existsByAlias(String alias);
}
