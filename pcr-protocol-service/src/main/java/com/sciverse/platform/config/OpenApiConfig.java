package com.sciverse.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pcrProtocolOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sciverse PCR Protocol Management API")
                        .description("RESTful API service for managing PCR protocols, execution parameters, and steps.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sciverse Solutions")
                                .email("support@sciverse.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
