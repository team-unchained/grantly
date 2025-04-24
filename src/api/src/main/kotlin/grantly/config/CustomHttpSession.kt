package grantly.config

import java.time.OffsetDateTime

data class CustomHttpSession(
    val token: String,
    val expiresAt: OffsetDateTime,
    val deviceId: String,
)
