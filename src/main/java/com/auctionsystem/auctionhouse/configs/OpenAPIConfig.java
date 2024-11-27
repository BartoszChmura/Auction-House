package com.auctionsystem.auctionhouse.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI productServiceAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Auction House API")
                        .description("This is the REST API for Auction House")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addTagsItem(new Tag().name("Authentication").description("Endpoints for user authentication"))
                .addTagsItem(new Tag().name("User").description("Endpoints for managing users"))
                .addTagsItem(new Tag().name("Item").description("Endpoints for managing items"))
                .addTagsItem(new Tag().name("Bid").description("Endpoints for bidding on items"))
                .addTagsItem(new Tag().name("Category").description("Endpoints for managing categories"))
                .addTagsItem(new Tag().name("Payment").description("Endpoints for payment processing"));
    }
}