package com.example.anifoxapi.model.responses

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Specify internal service error")
data class ErrorObject(
    @Schema(required = true)
    var code: String,
    @Schema(nullable = true)
    var message: String? = null
)
