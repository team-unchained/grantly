package grantly.app.application.port.`in`.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class DeleteAppClientParams(
    @field:NotNull
    val appId: Long,
    @field:NotBlank
    val clientId: String,
)
