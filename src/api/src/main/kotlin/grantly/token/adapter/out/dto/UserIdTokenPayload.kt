package grantly.token.adapter.out.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserIdTokenPayload(
    @JsonProperty("userId")
    val userId: Long,
)
