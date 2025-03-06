package grantly.user.domain

import java.time.OffsetDateTime

data class User(
    val id: Long = 0L,
    val email: String,
    var password: String? = null,
    var name: String,
    var lastLoginAt: OffsetDateTime? = null,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
) {
    fun hashPassword() {
        // TODO: 실제 해싱 알고리즘 적용
        password = "hashed"
    }
}
