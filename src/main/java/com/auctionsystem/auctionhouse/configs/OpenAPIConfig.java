package com.auctionsystem.auctionhouse.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI productServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Auction House API")
                        .description("This is the REST API for Auction House")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0")));
    }
}
