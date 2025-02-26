package grantly.user.domain

import java.time.OffsetDateTime

data class User(
    val id: Long = 0,
    val email: String,
    val password: String,
    var name: String,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var modifiedAt: OffsetDateTime? = null,
) {
    fun hashPassword(): String {
        // TODO: 실제 해싱 알고리즘 적용
        val hashedPwd = "hashed password"
        return hashedPwd
    }
}
