package com.mercedes.urlshortener.repository;

import com.mercedes.urlshortener.model.UrlMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UrlMappingRepositoryTest {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Test
    public void shouldInsertAndRetrieveUrlMapping() {
        UrlMapping urlMapping = new UrlMapping("https://example.com/test", "testAlias");
        urlMappingRepository.save(urlMapping);

        assertThat(urlMapping.getId(), notNullValue());

        UrlMapping retrievedMapping = urlMappingRepository.findById(urlMapping.getId()).get();
        assertThat(retrievedMapping.getId(), equalTo(urlMapping.getId()));
        assertThat(retrievedMapping.getAlias(), equalTo("testAlias"));
    }

}
