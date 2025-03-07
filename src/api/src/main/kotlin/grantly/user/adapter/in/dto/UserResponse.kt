package grantly.user.adapter.`in`.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val createdAt: String,
    val modifiedAt: String? = null,
)
