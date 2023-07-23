package com.example.catalogue.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(new Info()
                .title("Course Catalogue API")
                .version("1.0")
        );
        openAPI.addServersItem(new Server().url("/soapui-doc"));
        return openAPI;
    }
}
