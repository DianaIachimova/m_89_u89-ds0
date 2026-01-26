package com.example.insurance_app.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI insuranceAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Insurance App API")
                        .description("""
                                Backend API for Romanian insurance company managing building insurance policies.
                                
                                **Current Sprint:** Client and Building Management
                                
                                **User Roles:**
                                - Brokers: Manage clients and buildings
                                - Administrators: (Future sprint)
                                
                                **Features:**
                                - Client management (Individual/Company)
                                - Building management with geography integration
                                - Automatic CNP/CUI change tracking
                                - Owner immutability for buildings
                                """)
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Endava Training")
                                .email("training@endava.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Docker environment")
                ));
    }
}
