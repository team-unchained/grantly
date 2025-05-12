package grantly.user.adapter.`in`.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email

@Schema(description = "이메일 전송 요청 DTO")
data class SendEmailRequest(
    @field:Email(message = "Invalid email format")
    val email: String,
)
