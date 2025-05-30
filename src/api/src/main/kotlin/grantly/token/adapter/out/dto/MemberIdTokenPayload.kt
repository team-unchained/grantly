package grantly.token.adapter.out.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MemberIdTokenPayload(
    @JsonProperty("memberId")
    val memberId: Long,
)
