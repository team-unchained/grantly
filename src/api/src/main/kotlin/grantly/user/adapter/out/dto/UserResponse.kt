package grantly.user.adapter.out.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val createdAt: String,
    val modifiedAt: String? = null,
)
