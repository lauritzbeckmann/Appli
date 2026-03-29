package com.mercedes.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.mercedes.urlshortener.service.UrlService;

@SpringBootTest
class UrlShortenerApplicationTests {

	@Autowired
	private UrlService urlService;

	@Test
	void contextLoads() {
		// Context loads successfully
	}

}
