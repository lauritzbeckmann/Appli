package com.mercedes.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercedes.urlshortener.dto.ShortenRequest;
import com.mercedes.urlshortener.service.UrlService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UrlControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UrlService urlService;

    @Test
    public void shouldReturnCreatedStatusForValidUrl() throws Exception {
        ShortenRequest request = new ShortenRequest("https://example.com/test/path");

        mvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").exists())
                .andExpect(jsonPath("$.shortUrl", startsWith("http")));
    }

    @Test
    public void shouldReturnConflictForDuplicateAlias() throws Exception {
        ShortenRequest request1 = new ShortenRequest("https://example.com/page1", "myalias");
        ShortenRequest request2 = new ShortenRequest("https://example.com/page2", "myalias");

        mvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request1)))
                .andExpect(status().isCreated());

        mvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request2)))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturnBadRequestForInvalidUrl() throws Exception {
        ShortenRequest request = new ShortenRequest("not a valid url");

        mvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    public static String asJsonString(final Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }

}
