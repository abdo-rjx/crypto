package com.example.crypto.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

    /**
     * Registers the "cryptoFilter" used by @JsonFilter on the Crypto entity.
     * By default, all fields are serialized EXCEPT "clePrivee" (the private key),
     * which is sensitive and should never be exposed in API responses.
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();

        SimpleFilterProvider filters = new SimpleFilterProvider();
        filters.addFilter("cryptoFilter",
                SimpleBeanPropertyFilter.serializeAllExcept("clePrivee"));

        mapper.setFilterProvider(filters);
        converter.setObjectMapper(mapper);
        return converter;
    }
}
