package com.mercedes.urlshortener.controller;

import com.mercedes.urlshortener.service.UrlService;
import com.mercedes.urlshortener.dto.ShortenRequest;
import com.mercedes.urlshortener.dto.ShortenResponse;
import com.mercedes.urlshortener.dto.AnalyticsResponse;
import com.mercedes.urlshortener.exception.InvalidUrlException;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class UrlController {

    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> index() {
        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("message", "URL Shortener API is running");
        payload.put("shorten", "POST /shorten");
        payload.put("redirect", "GET /{alias}");
        payload.put("analytics", "GET /analytics/{alias}");
        payload.put("health", "GET /actuator/health");
        return ResponseEntity.ok(payload);
    }

    /**
     * Creates a shortened URL with optional custom alias
     *
     * @param shortenRequest contains the full URL and optional alias
     * @param request HTTP request to extract base URL
     * @return shortened URL response
     */
    @PostMapping("/shorten")
    public ResponseEntity<ShortenResponse> shortenUrl(
            @RequestBody ShortenRequest shortenRequest,
            HttpServletRequest request) {

        // Validate URL format
        UrlValidator validator = new UrlValidator(new String[]{"http", "https"});
        if (!validator.isValid(shortenRequest.getFullUrl())) {
            logger.error("Invalid URL format provided");
            throw new InvalidUrlException("Invalid URL format");
        }

        // Create shortened URL
        ShortenResponse shortenResponse = urlService.shortenUrl(shortenRequest);

        // Prepend base URL
        String baseUrl = getBaseUrl(request.getRequestURL().toString());
        shortenResponse.setShortUrl(baseUrl + shortenResponse.getShortUrl());

        logger.debug(String.format("ShortUrl created: %s", shortenResponse.getShortUrl()));
        return ResponseEntity.status(HttpStatus.CREATED).body(shortenResponse);
    }

    /**
     * Redirects to the original URL for a given short URL alias
     *
     * @param alias the short URL alias
     * @param response HTTP response to redirect
     */
    @GetMapping("/{alias}")
    public void redirect(@PathVariable String alias, HttpServletResponse response) {
        try {
            String fullUrl = urlService.getFullUrl(alias);
            logger.info(String.format("Redirecting alias %s to %s", alias, fullUrl));
            response.sendRedirect(fullUrl);
        } catch (IOException e) {
            logger.error("Error redirecting to full URL", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing redirect");
            } catch (IOException ioException) {
                logger.error("Error sending error response", ioException);
            }
        }
    }

    /**
     * Retrieves analytics for a shortened URL
     *
     * @param alias the short URL alias
     * @param startDate optional start date (yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate optional end date (yyyy-MM-dd'T'HH:mm:ss)
     * @return analytics response with click statistics
     */
    @GetMapping("/analytics/{alias}")
    public ResponseEntity<AnalyticsResponse> getAnalytics(
            @PathVariable String alias,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        try {
            LocalDateTime start = null;
            LocalDateTime end = null;

            if (startDate != null || endDate != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                if (startDate != null) {
                    start = LocalDateTime.parse(startDate, formatter);
                }
                if (endDate != null) {
                    end = LocalDateTime.parse(endDate, formatter);
                }
            }

            logger.info(String.format("Fetching analytics for alias %s", alias));
            AnalyticsResponse analytics = urlService.getAnalytics(alias, start, end);
            return ResponseEntity.ok(analytics);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format provided", e);
            throw new InvalidUrlException("Invalid date format. Use yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    /**
     * Extracts the base URL (protocol://host:port) from a full URL
     *
     * @param url the full URL
     * @return base URL with trailing slash
     */
    private String getBaseUrl(String url) {
        try {
            URL reqUrl = new URL(url);
            String protocol = reqUrl.getProtocol();
            String host = reqUrl.getHost();
            int port = reqUrl.getPort();

            if (port == -1) {
                return String.format("%s://%s/", protocol, host);
            } else {
                return String.format("%s://%s:%d/", protocol, host, port);
            }
        } catch (MalformedURLException e) {
            logger.error("Error extracting base URL", e);
            throw new InvalidUrlException("Invalid request URL");
        }
    }
}
