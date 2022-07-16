package com.example.anifoxapi.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springdoc.core.GroupedOpenApi

@Configuration
class SwaggerConfig {

    @Bean
    fun publicApiV1(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("anifox-api-v2")
            .pathsToMatch("/api2/**")
            .build()
    }

    @Bean
    fun aniFoxOpenAPI(): OpenAPI? {
        return OpenAPI()
            .info(
                Info().title("AniFox API")
                    .description("Application de gestion des entites geographiques")
                    .version("v0.0.1")
                    .license(License().name("GPL 2.0").url("https://github.com/bangaromaric/api-geo/blob/main/LICENSE"))
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Wiki Documentation")
                    .url("#")
            )
    }
}