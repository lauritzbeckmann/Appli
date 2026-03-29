package com.mercedes.urlshortener.service;

import com.mercedes.urlshortener.repository.UrlMappingRepository;
import com.mercedes.urlshortener.repository.ClickEventRepository;
import com.mercedes.urlshortener.model.UrlMapping;
import com.mercedes.urlshortener.model.ClickEvent;
import com.mercedes.urlshortener.dto.ShortenRequest;
import com.mercedes.urlshortener.dto.ShortenResponse;
import com.mercedes.urlshortener.dto.AnalyticsResponse;
import com.mercedes.urlshortener.exception.AliasAlreadyExistsException;
import com.mercedes.urlshortener.exception.UrlNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UrlService {

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

    private final UrlMappingRepository urlMappingRepository;
    private final ClickEventRepository clickEventRepository;

    private static final String ALIAS_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int RANDOM_ALIAS_LENGTH = 6;

    @Autowired
    public UrlService(UrlMappingRepository urlMappingRepository, ClickEventRepository clickEventRepository) {
        this.urlMappingRepository = urlMappingRepository;
        this.clickEventRepository = clickEventRepository;
    }

    /**
     * Generates a random alias of 6 characters
     *
     * @return a random 6-character alias
     */
    private String generateRandomAlias() {
        StringBuilder alias = new StringBuilder();
        for (int i = 0; i < RANDOM_ALIAS_LENGTH; i++) {
            int randomIndex = (int) (Math.random() * ALIAS_ALPHABET.length());
            alias.append(ALIAS_ALPHABET.charAt(randomIndex));
        }
        // Ensure alias is unique
        while (urlMappingRepository.existsByAlias(alias.toString())) {
            StringBuilder newAlias = new StringBuilder();
            for (int i = 0; i < RANDOM_ALIAS_LENGTH; i++) {
                int randomIndex = (int) (Math.random() * ALIAS_ALPHABET.length());
                newAlias.append(ALIAS_ALPHABET.charAt(randomIndex));
            }
            alias = newAlias;
        }
        return alias.toString();
    }

    /**
     * Validates that an alias is available
     *
     * @param alias the alias to validate
     * @return true if alias is available (not used), false otherwise
     */
    public boolean isAliasAvailable(String alias) {
        return !urlMappingRepository.existsByAlias(alias);
    }

    /**
     * Gets the full URL for a given alias and tracks the click
     *
     * @param alias the shortened alias
     * @return the full URL
     */
    public String getFullUrl(String alias) {
        logger.info(String.format("Retrieving full url for alias %s", alias));

        Optional<UrlMapping> urlMapping = urlMappingRepository.findByAlias(alias);

        if (!urlMapping.isPresent()) {
            logger.error(String.format("Alias '%s' not found", alias));
            throw new UrlNotFoundException("Shortened URL not found");
        }

        // Track the click
        ClickEvent click = new ClickEvent(urlMapping.get().getId());
        clickEventRepository.save(click);
        logger.debug(String.format("Click tracked for alias %s", alias));

        return urlMapping.get().getFullUrl();
    }

    /**
     * Creates a shortened URL with optional custom alias
     *
     * @param shortenRequest ShortenRequest object (may contain custom alias)
     * @return ShortenResponse object with the alias
     */
    public ShortenResponse shortenUrl(ShortenRequest shortenRequest) {

        logger.info("Processing URL shortening request");
        String alias = shortenRequest.getAlias();

        UrlMapping savedUrl = null;

        // Use provided alias or generate random one
        if (alias != null && !alias.trim().isEmpty()) {
            logger.info(String.format("Custom alias provided: %s", alias));
            if (!isAliasAvailable(alias)) {
                logger.error(String.format("Custom alias '%s' is already taken", alias));
                throw new AliasAlreadyExistsException("Alias already taken");
            }
        } else {
            alias = generateRandomAlias();
            logger.info(String.format("Generated random alias: %s", alias));
        }

        // Check if URL already exists (only if same full URL)
        List<UrlMapping> existingMappings = urlMappingRepository.findByFullUrl(shortenRequest.getFullUrl());

        if (existingMappings.isEmpty()) {
            logger.info(String.format("Saving URL %s with alias %s to database", shortenRequest.getFullUrl(), alias));
            UrlMapping newUrl = new UrlMapping(shortenRequest.getFullUrl(), alias);
            savedUrl = urlMappingRepository.save(newUrl);
            logger.debug(savedUrl.toString());
        } else {
            savedUrl = existingMappings.get(0);
            logger.info(String.format("URL already exists in the database: %s", savedUrl.getAlias()));
        }

        return new ShortenResponse(savedUrl.getAlias());
    }

    /**
     * Get analytics for a shortened URL
     *
     * @param alias the shortened URL alias
     * @param startDate optional start date for filtering clicks
     * @param endDate optional end date for filtering clicks
     * @return AnalyticsResponse with click statistics
     */
    public AnalyticsResponse getAnalytics(String alias, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info(String.format("Retrieving analytics for alias %s", alias));

        Optional<UrlMapping> urlMapping = urlMappingRepository.findByAlias(alias);

        if (!urlMapping.isPresent()) {
            logger.error(String.format("Alias '%s' not found", alias));
            throw new UrlNotFoundException("Shortened URL not found");
        }

        long clickCount;
        if (startDate != null && endDate != null) {
            logger.info(String.format("Counting clicks for alias %s between %s and %s", alias, startDate, endDate));
            clickCount = clickEventRepository.countByUrlIdAndDateRange(urlMapping.get().getId(), startDate, endDate);
        } else {
            logger.info(String.format("Counting all clicks for alias %s", alias));
            clickCount = clickEventRepository.countByUrlId(urlMapping.get().getId());
        }

        logger.debug(String.format("Found %d clicks for alias %s", clickCount, alias));
        return new AnalyticsResponse(
                urlMapping.get().getAlias(),
                urlMapping.get().getFullUrl(),
                clickCount,
                urlMapping.get().getCreatedAt()
        );
    }
}
