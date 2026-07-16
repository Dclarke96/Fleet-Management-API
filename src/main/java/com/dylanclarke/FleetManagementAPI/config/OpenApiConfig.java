package com.dylanclarke.FleetManagementAPI.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fleetManagementApi() {

        return new OpenAPI()
                .info(new Info()
                        .title("Fleet Management API")
                        .description(
                                "REST API for managing vehicles, maintenance records, and fleet operations."
                        )
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("Dylan Clarke")
                                .url("https://github.com/Dclarke96"))
                        .license(new License()
                                .name("MIT License")
                        )
                );
    }
}