package com.mercedes.urlshortener.dto;

public class ShortenRequest {

    private String fullUrl;
    private String alias;

    public ShortenRequest() {
    }

    public ShortenRequest(String fullUrl) {
        this.fullUrl = fullUrl;
        this.alias = null;
    }

    public ShortenRequest(String fullUrl, String alias) {
        this.fullUrl = fullUrl;
        this.alias = alias;
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
}
