package com.school.mamagement.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI schoolManagementOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("School Management System API")
                        .description("""
                                REST API for the School Management System.

                                API version: v1.
                                Protected endpoints use JWT Bearer authentication.
                                Use the authentication login endpoint to obtain an access token.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("School Management System Team")))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}
