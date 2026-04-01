package com.example.crypto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI cryptoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crypto Portfolio API")
                        .description("REST API for managing a cryptocurrency portfolio")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Crypto Team")
                                .email("contact@crypto.example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
